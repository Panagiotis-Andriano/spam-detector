package com.panagiotis.spam_detector.ml;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Feature Extraction για το ML model
 */
public class FeatureExtractor {
    
    public double[] extractFeaturesArray(String message) {
        Map<String, Object> features = extractFeatures(message);
        System.out.println("DEBUG Features for: '" + message + "'");
        features.forEach((key, value) -> System.out.println("   " + key + ": " + value));
        return new double[] {
            (double) features.get("spam_keyword_count"),
            (double) features.get("url_present"),
            (double) features.get("excessive_caps"),
            (double) features.get("multiple_exclamations"),
            (double) features.get("phone_pattern"),
            (double) features.get("urgency_words"),
            (double) features.get("short_length"),
            (double) features.get("free_mentions"),
            (double) features.get("suspicious_patterns"),
            (double) features.get("money_mentions"),
            (double) features.get("spam_ratio")
        };
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

    public double calculateRuleBasedProbability(Map<String, Object> features) {
        double baseScore = 0.0;

        baseScore += (double) features.get("spam_keyword_count") * 0.20;
        baseScore += (double) features.get("url_present") * 0.15;
        baseScore += (double) features.get("excessive_caps") * 0.10;
        baseScore += (double) features.get("urgency_words") * 0.12;
        baseScore += (double) features.get("free_mentions") * 0.08;
        baseScore += (double) features.get("money_mentions") * 0.10;

        return Math.min(1.0, baseScore);
    }

    private int countSpamKeywords(String message) {
        System.out.println("Counting spam keywords in: " + message);
        Set<String> spamWords = Set.of(
            "free", "win", "prize", "cash", "urgent", "click", 
            "subscribe", "bonus", "won", "money", "gift", "offer", "limited", 
            "now", "winner", "selected", "congratulations", "guaranteed", 
            "risk-free", "act now", "apply now", "call now", "buy now", "order now", 
            "discount", "save", "deal", "bargain", "clearance", "credit", "loan", 
            "mortgage", "investment", "profit", "miracle", "magic", "secret", "trick", 
            "method", "weight loss", "diet", "pills", "supplement", "enhancement", 
            "luxury", "exclusive", "premium", "vip", "membership", "unlimited", 
            "instant", "fast", "quick", "easy", "income", "salary", "earn", 
            "make money", "get rich", "trial", "sample", "shipping", "order", 
            "purchase", "click here", "visit now", "sign up", "register", 
            "enroll", "δωρεάν", "κέρδισε", "επείγον", "κλίκ", "μπόνους", 
            "δώρο", "προσωρινή", "τώρα", "νικητής", "επιλέχθηκε", "συγχαρητήρια", 
            "εγγυημένο", "χωρίς ρίσκο", "δράσε τώρα", "κλήσε τώρα", "αγόρασε τώρα", 
            "παράγγειλε τώρα", "έκπτωση", "εκκαθάριση", "προσφορά", "συμφωνία", 
            "δάνειο", "πίστωση", "επένδυση", "κέρδος", "κερδοσκοπία", "θαύμα", 
            "μαγικό", "μυστικό", "συνταγή", "μέθοδος", "αδυνάτισμα", "δίαιτα", 
            "χάπια", "συμπλήρωμα", "ενίσχυση", "πολυτελές", "αποκλειστικό", 
            "μέλος", "συνδρομή", "απεριόριστο", "άμεσο", "γρήγορο", "εύκολο", 
            "εισόδημα", "μισθός", "κερδίστε", "βγάλε λεφτά", "γίνε πλούσιος", 
            "δοκιμή", "δείγμα", "αποστολή", "παραγγελία", "αγορά", "κλίκ εδώ", 
            "επισκεφτείτε τώρα", "εγγραφείτε", "εγγραφή", "εγγράψου"
        );
        int count = 0;
        for (String word : spamWords) {
            if (message.contains(word)) {
                System.out.println("Found spam word: " + word);
                count++;
            }
        }
        System.out.println("Total spam keywords: " + count);
        return count;
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
        System.out.println("Checking urgency words in: " + message);
        boolean found = Pattern.compile("urgent|immediately|now|quick|fast|instant|hurry|limited time|επείγον|αμέσως|τώρα|γρήγορα|βιαστικά|περιορισμένος χρόνος").matcher(message).find();
        System.out.println("Urgency words found: " + found);
        return found;
    }

    private boolean containsFreeMentions(String message) {
        return Pattern.compile("\\bfree\\b|\\bδωρεάν\\b").matcher(message).find();
    }

    private double calculateSuspiciousPatterns(String message) {
        int patterns = 0;
        if (Pattern.compile("\\b[A-ZΑ-Ω]{4,}\\b").matcher(message).find()) patterns++;
        if (Pattern.compile("\\d+\\s*[€$]").matcher(message).find()) patterns++;
        if (Pattern.compile("\\b(\\w+)\\b.*\\b\\1\\b", Pattern.CASE_INSENSITIVE).matcher(message).find()) patterns++;

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
