package com.panagiotis.spam_detector.ml;

import java.util.Arrays;
import java.util.List;

/**
 * Training dataset για το ML model
 */
public class TrainingData {
    
    public static List<SpamTrainingExample> getTrainingData() {
        return Arrays.asList(
            new SpamTrainingExample("Κέρδισε 1000€ τώρα! Κλίκ εδώ", true),
            new SpamTrainingExample("ΔΩΡΕΑΝ χρήματα! Νίκησε το κουπόνι", true),
            new SpamTrainingExample("Επείγον! Κέρδισε iPhone ΔΩΡΕΑΝ", true),
            new SpamTrainingExample("ΠΡΟΣΟΧΗ! Κλείσε τώρα τηλεφωνικά", true),
            new SpamTrainingExample("Νέα προσφορά! Μόνο σήμερα 50%", true),
            new SpamTrainingExample("WIN $1000 NOW! Click here", true),
            new SpamTrainingExample("FREE money! Get your bonus", true),
            new SpamTrainingExample("URGENT! You won iPhone FREE", true),
            new SpamTrainingExample("ATTENTION! Call now limited time", true),
            new SpamTrainingExample("Special offer! 50% discount today", true),
            new SpamTrainingExample("Γεια σου, πώς είσαι σήμερα;", false),
            new SpamTrainingExample("Συναντηθούμε αύριο στις 5", false),
            new SpamTrainingExample("Ο καιρός είναι πολύ ωραίος σήμερα", false),
            new SpamTrainingExample("Θα πας στο γυμναστήριο απόψε;", false),
            new SpamTrainingExample("Το meeting αύριο είναι ακυρωμένο", false),
            new SpamTrainingExample("Hello, how are you today?", false),
            new SpamTrainingExample("Let's meet tomorrow at 5pm", false),
            new SpamTrainingExample("The weather is really nice today", false),
            new SpamTrainingExample("Are you going to the gym tonight?", false),
            new SpamTrainingExample("Tomorrow's meeting is cancelled", false)
        );
    }
    
    public static class SpamTrainingExample {
        private final String message;
        private final boolean isSpam;

        public SpamTrainingExample(String message, boolean isSpam) {
            this.message = message;
            this.isSpam = isSpam;
        }

        public String getMessage() { return message; }
        public boolean isSpam() { return isSpam; }
    }
}
    
