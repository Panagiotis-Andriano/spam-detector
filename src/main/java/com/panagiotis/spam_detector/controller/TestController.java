package com.panagiotis.spam_detector.controller;

import java.util.*;
import java.util.regex.Pattern;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.panagiotis.spam_detector.service.MlSpamService;

@Controller
public class TestController {

    private final MlSpamService mlSpamService;

    public TestController(MlSpamService mlSpamService) {
        this.mlSpamService = mlSpamService;
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
        String message = request.get("message");
        return analyzeWithMl(message);
    }
   
    private Map<String, Object> analyzeWithMl(String message) {
        Map<String, Object> features = mlSpamService.extractFeatures(message);

        //ML predictions
        double mlProbability = mlSpamService.predictSpamProbability(features);
        boolean isSpam = mlProbability > 0.5;

        boolean ruleBasedSpam = containsObviousSpamPatterns(message.toLowerCase());
        boolean finalDecision = isSpam || ruleBasedSpam;
        double confidence = ruleBasedSpam ? Math.max(mlProbability, 0.85) : mlProbability;

        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("isSpam", finalDecision);
        response.put("confidence", Math.round(confidence * 100) / 100.0);
        response.put("mlProbability", Math.round(mlProbability * 100) / 100.0);
        response.put("language", detectLanguage(message));
        response.put("features", features);
        response.put("featureCount", features.size());

        return response;
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

