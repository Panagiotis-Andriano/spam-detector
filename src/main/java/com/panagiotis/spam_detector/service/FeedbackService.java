package com.panagiotis.spam_detector.service;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.springframework.stereotype.Service;
import com.panagiotis.spam_detector.entity.Feedback;
import com.panagiotis.spam_detector.repository.FeedbackRepository;

/**
 * Service κλάση για διαχείρηση feedback λειτουργιών
 */
@Service
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;

    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    //Αποθήκευση νέο feedback από χρήστη
    public Feedback saveFeedback(Long spamCheckId, boolean userSaysIsSpam, boolean systemSaysIsSpam, String userComment, String messageLanguage, Double systemConfidence) {
        Feedback feedback = new Feedback(spamCheckId, userSaysIsSpam, systemSaysIsSpam, userComment, messageLanguage, systemConfidence);
        return feedbackRepository.save(feedback);
    }

    //Επιστροφή στατιστικών για τα feedback
    public Map<String, Object> getFeedbackStats() {
        Map<String, Object> stats = new HashMap<>();

        long totalFeedback = feedbackRepository.count();
        long totalDiscrepancies = feedbackRepository.countDiscrepancies();

        stats.put("totalFeedback", totalFeedback);
        stats.put("totalDiscrepancies", totalDiscrepancies);
        stats.put("accuracyRate", totalFeedback > 0 ? ((double) (totalFeedback - totalDiscrepancies) / totalFeedback) * 100 : 0);
        List<Object[]> languageStats = feedbackRepository.countByLanguage();
        Map<String, Long> feedbackByLanguage = new HashMap<>();
        for (Object[] stat : languageStats) {
            feedbackByLanguage.put((String) stat[0], (Long) stat[1]);
        }
        stats.put("feedbackByLanguage", feedbackByLanguage);

        return stats;
    }

    //Επιστρέφει όλα τα feedback με discrepancies
    public List<Feedback> getDiscrepancies() {
        return feedbackRepository.findDiscrepancies();
    }
}
