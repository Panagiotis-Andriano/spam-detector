package com.panagiotis.spam_detector.controller;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.panagiotis.spam_detector.entity.Feedback;
import com.panagiotis.spam_detector.entity.SpamCheckHistory;
import com.panagiotis.spam_detector.ml.SpamClassifier;
import com.panagiotis.spam_detector.repository.SpamCheckHistoryRepository;
import com.panagiotis.spam_detector.service.FeedbackService;
import com.panagiotis.spam_detector.service.MlSpamService;



@Controller
public class TestController {

    private final MlSpamService mlSpamService;
    private final SpamCheckHistoryRepository historyRepository;
    private final FeedbackService feedbackService;


    public TestController(MlSpamService mlSpamService, SpamCheckHistoryRepository historyRepository, FeedbackService feedbackService) {
        this.mlSpamService = mlSpamService;
        this.historyRepository = historyRepository;
        this.feedbackService = feedbackService;
    }

    @GetMapping("/")
    public String home() {
        return "forward:/index.html";
    }
    
    @GetMapping("/api/detect")
    @ResponseBody
    public Map<String, Object> detectSpamGet(@RequestParam String message) {
        return analyzeWithMl(message);
    }

    @PostMapping("/api/detect")
    @ResponseBody
    public Map<String, Object> detectSpamPost(@RequestBody Map<String, String> request) {
        try {
            String message = request.get("message");
            if (message == null || message.trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Message is required");
                return errorResponse;
            }
            return analyzeWithMl(message);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Processing failed: " + e.getMessage());
            return errorResponse;
        }
    }

    @GetMapping("/api/test")
    @ResponseBody
    public Map<String, String> testEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Backend is working!");
        return response;
    }       

    //Κεντρική μέθοδος ανάλυσης
    private Map<String, Object> analyzeWithMl(String message) {
        Map<String, Object> features = mlSpamService.extractFeatures(message);

        //ML predictions
        SpamClassifier.SpamPrediction mlPrediction = mlSpamService.predictWithML(message);
        double mlProbability = mlPrediction.getProbability();
        boolean isSpam = mlPrediction.isSpam();

        boolean ruleBasedSpam = containsObviousSpamPatterns(message.toLowerCase());
        boolean finalDecision = isSpam || ruleBasedSpam;
        double confidence = ruleBasedSpam ? Math.max(mlProbability, 0.85) : mlProbability;
        String language = detectLanguage(message);

        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("isSpam", finalDecision);
        response.put("confidence", Math.round(confidence * 100) / 100.0);
        response.put("mlProbability", Math.round(mlProbability * 100) / 100.0);
        response.put("language", language);
        response.put("features", features);
        response.put("featureCount", features.size());

         mlSpamService.saveToHistory(message, finalDecision, confidence, language);

        return response;
    }

    //Ιστορικό ελέγχων
    @GetMapping("/api/history")
    @ResponseBody
    public Map<String, Object> getHistory() {
        List<SpamCheckHistory> history = historyRepository.findByOrderByCreatedAtDesc();

        long totalSpam = historyRepository.countByIsSpam(true);
        long totalNonSpam = historyRepository.countByIsSpam(false);

        Map<String, Object> response = new HashMap<>();
        response.put("totalChecks", history.size());
        response.put("spamCount", totalSpam);
        response.put("nonSpamCount", totalNonSpam);
        response.put("history", history.stream().map(this::convertToMap).collect(Collectors.toList()));

        return response;
    }

    //Διαγραφή του ιστορικού
    @DeleteMapping("/api/history")
    @ResponseBody
    public Map<String, Object> clearHistory() {
        long countBefore = historyRepository.count();
        historyRepository.deleteAll();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "History cleared");
        response.put("deletedRecords", countBefore);
        return response;
    }

    //Αποθήκευση feedback από χρήστη
    @PostMapping("/api/feedback")
    @ResponseBody
    public Map<String, Object> saveFeedback(@RequestBody Map<String, Object> feedbackRequest) {
        try {
            Long spamCheckId = Long.valueOf(feedbackRequest.get("spamCheckId").toString());
            Boolean userSaysIsSpam = Boolean.valueOf(feedbackRequest.get("userSaysIsSpam").toString());
            Boolean systemSaysIsSpam = Boolean.valueOf(feedbackRequest.get("systemSaysIsSpam").toString());
            String userComment = (String) feedbackRequest.get("userComment");
            String messageLanguage = (String) feedbackRequest.get("messageLanguage");
            Double systemConfidence = Double.valueOf(feedbackRequest.get("systemConfidence").toString());

            if(spamCheckId == null || userSaysIsSpam == null || systemSaysIsSpam == null) {
                throw new IllegalArgumentException("Missing required fields");
            }

            Feedback feedback = feedbackService.saveFeedback(spamCheckId, userSaysIsSpam, systemSaysIsSpam, userComment, messageLanguage, systemConfidence);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("feedbackId", feedback.getId());
            response.put("message", "Feedback saved succesfully");

            return response;
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error saving feedback: " + e.getMessage());
            return response;
        }
    }

    @GetMapping("/api/feedback/stats")
    @ResponseBody
    public Map<String, Object> getFeedbackStats() {
        return feedbackService.getFeedbackStats();
    }

    @GetMapping("/api/feedback/discrepancies")
    @ResponseBody
    public Map<String, Object> getDiscrepancies() {
        List<Feedback> discrepancies = feedbackService.getDiscrepancies();
        
        Map<String, Object> response = new HashMap<>();
        response.put("totalDiscrepancies", discrepancies.size());
        response.put("discrepancies", discrepancies.stream()
            .map(this::convertFeedbackToMap)
            .toList());
        
        return response;
    }

    //Μέθοδος για μετατροπή Feedback σε Map για καθαρό JSON responses
    private Map<String, Object> convertFeedbackToMap(Feedback feedback) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", feedback.getId());
        map.put("spamCheckId", feedback.getSpamCheckId());
        map.put("userSaysIsSpam", feedback.isUserSaysIsSpam());
        map.put("systemSaysIsSpam", feedback.isSystemSaysIsSpam());
        map.put("userComment", feedback.getUserComment());
        map.put("messageLanguage", feedback.getMessageLanguage());
        map.put("systemConfidence", feedback.getSystemConfidence());
        map.put("hasDiscrepancy", feedback.hasDiscrepancy());
        map.put("createdAt", feedback.getCreatedAt().toString());
        return map;
    }
    
    //Μέθοδος για μετατροπή Entity σε Map για καθαρό JSON responses
    private Map<String, Object> convertToMap(SpamCheckHistory history) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", history.getId());
        map.put("message", history.getMessage());
        map.put("isSpam", history.isSpam());
        map.put("confidence", history.getConfidence());
        map.put("language", history.getLanguage());
        map.put("createdAt", history.getCreatedAt().toString());
        return map;
    }

    private boolean containsObviousSpamPatterns(String message) {
        return Pattern.compile("(win|κέρδισε).*\\d+.*€?|(free|δωρεάν).*(money|χρήματα)|click.*now|κλίκ.*τώρα").matcher(message).find();
    }

    private String detectLanguage(String message) {
        if (message.matches(".*[α-ωΑ-Ω].*")) {
            return "greek";
        }
        return "english";
    }
}

