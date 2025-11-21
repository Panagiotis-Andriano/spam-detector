package com.panagiotis.spam_detector.service;

import java.util.regex.Pattern;
import java.util.*;
import org.springframework.stereotype.Service;
import com.panagiotis.spam_detector.entity.SpamCheckHistory;
import com.panagiotis.spam_detector.ml.SpamClassifier;
import com.panagiotis.spam_detector.repository.SpamCheckHistoryRepository;

/**
 * Κύριο service class για spam detection
 */

@Service
public class MlSpamService {
    private final SpamCheckHistoryRepository historyRepository;
    private final SpamClassifier spamClassifier;

    public MlSpamService(SpamCheckHistoryRepository historyRepository, SpamClassifier spamClassifier) {
        this.historyRepository = historyRepository;
        this.spamClassifier = spamClassifier;
    }

    //Αποθήκευση ενός έλεγχου spam στο ιστορικό της βάσης δεδομένων
    public void saveToHistory(String message, boolean isSpam, double confidence, String language) {
        SpamCheckHistory history = new SpamCheckHistory(message, isSpam, confidence, language);

        historyRepository.save(history);
    }

    public Map<String, Object> extractFeatures(String message) {
        Map<String, Object> features = new HashMap<>();
        String lowerMessage = message.toLowerCase();

        int spamKeywords = countSpamKeywords(lowerMessage);
        features.put("spam_keyword_count", normalizeCount(spamKeywords, 8));
        features.put("url_present", containsUrl(lowerMessage) ? 1.0 : 0.0);
        features.put("excessive_caps", calculateCapsRatio(message));
        features.put("multiple_exclamations", calculateExclamationScore(message));
        features.put("phone_pattern", containsPhonePattern(lowerMessage) ? 1.0 : 0.0);
        features.put("urgency_words", containsUrgencyWords(lowerMessage) ? 1.0 : 0.0);
        features.put("short_length", message.length() < 30 ? 1.0 : 0.0);
        features.put("free_mentions", containsFreeMentions(lowerMessage) ? 1.0 : 0.0);
        features.put("suspicious_patterns", calculateSuspiciousPatterns(message));
        features.put("money_mentions", containsMoneyMentions(lowerMessage) ? 1.0 : 0.0);
        features.put("spam_ratio", calculateSpamRatio(message, spamKeywords));

        return features;
    }

    public SpamClassifier.SpamPrediction predictWithML(String message) {
        return spamClassifier.predict(message);
    }

    private int countSpamKeywords(String message) {
        Set<String> spamWords = Set.of(
            "free", "win", "prize", "cash", "urgent", "click", "subscribe",
            "bonus", "won", "money", "gift", "offer", "limited", "now",
            "winner", "selected", "congratulations", "guaranteed", "risk-free",
            "act now", "apply now", "call now", "buy now", "order now", 
            "discount", "save", "deal", "bargain", "clearance",
            "credit", "loan", "mortgage", "investment", "profit",
            "miracle", "magic", "secret", "trick", "method",
            "weight loss", "diet", "pills", "supplement", "enhancement",
            "luxury", "exclusive", "premium", "vip", "membership",
            "unlimited", "instant", "fast", "quick", "easy",
            "income", "salary", "earn", "make money", "get rich",
            "trial", "sample", "shipping", "order", "purchase",
            "click here", "visit now", "sign up", "register", "enroll",
            "δωρεάν", "κέρδισε", "επείγον", "κλίκ","μπόνους", 
            "δώρο", "προσωρινή", "τώρα", "νικητής",
            "επιλέχθηκε", "συγχαρητήρια", "εγγυημένο", "χωρίς ρίσκο",
            "δράσε τώρα", "κλήσε τώρα", "αγόρασε τώρα", "παράγγειλε τώρα",
            "έκπτωση", "εκκαθάριση", "προσφορά", "συμφωνία",
            "δάνειο", "πίστωση", "επένδυση", "κέρδος", "κερδοσκοπία",
            "θαύμα", "μαγικό", "μυστικό", "συνταγή", "μέθοδος",
            "αδυνάτισμα", "δίαιτα", "χάπια", "συμπλήρωμα", "ενίσχυση",
            "πολυτελές", "αποκλειστικό", "μέλος", "συνδρομή",
            "απεριόριστο", "άμεσο", "γρήγορο", "εύκολο",
            "εισόδημα", "μισθός", "κερδίστε", "βγάλε λεφτά", "γίνε πλούσιος",
            "δοκιμή", "δείγμα", "αποστολή", "παραγγελία", "αγορά",
            "κλίκ εδώ", "επισκεφτείτε τώρα", "εγγραφείτε", "εγγραφή", "εγγράψου"
        );
        return (int) spamWords.stream().filter(message::contains).count();
    }

    private double normalizeCount(int count, int max) {
        return Math.min(1.0, (double) count / max);
    }

    private boolean containsUrl(String message) {
        return Pattern.compile("https?://|www\\.[a-zA-Z0-9]|\\.(com|gr|org|net|io|info)").matcher(message).find();
    }

    private double calculateCapsRatio(String message) {
        String capsOnly = message.replaceAll("[^A-ZΑ-Ω]", "");
        return Math.min(1.0, (double) capsOnly.length() / message.length() * 3);
    }

    private double calculateExclamationScore(String message) {
        long exclamationCount = message.chars().filter(ch -> ch == '!').count();
        return Math.min(1.0, exclamationCount / 3.0);
    }

    private boolean containsPhonePattern(String message) {
        return Pattern.compile("\\+30|69\\d{8}|2\\d{9}|τηλέφωνο|κλήση|κινητό").matcher(message).find();
    }

    private boolean containsUrgencyWords(String message) {
        return Pattern.compile("urgent|immediately|now|quick|fast|instant|hurry|limited time|επείγον|αμέσως|τώρα|γρήγορα|βιαστικά|περιορισμένος χρόνος").matcher(message).find();
    }

    private boolean containsFreeMentions(String message) {
        return Pattern.compile("\\bfree\\b|\\bδωρεάν\\b").matcher(message).find();
    }

    private double calculateSuspiciousPatterns(String message) {
        int patterns = 0;
        if (Pattern.compile("\\b[A-ZΑ-Ω]{4,}\\b").matcher(message).find()) {
            patterns++;
        }

        if (Pattern.compile("\\d+\\s*[€$]").matcher(message).find()) {
            patterns++;
        }

        if (Pattern.compile("\\b(\\w+)\\b.*\\b\\1\\b", Pattern.CASE_INSENSITIVE).matcher(message).find()) {
            patterns++;
        }

        return Math.min(1.0, patterns / 3.0);
    }

    private boolean containsMoneyMentions(String message) {
        return Pattern.compile("\\b(money|cash|euros?|dollars?|€|\\$|χρήματα|ευρώ|λεπτά)\\b").matcher(message).find();
    }

    private double calculateSpamRatio(String message, int spamKeywords) {
        int totalWords = message.split("\\s+").length;
        if (totalWords == 0) return 0.0;
        return Math.min(1.0, (double) spamKeywords / totalWords * 3);
    }
}
