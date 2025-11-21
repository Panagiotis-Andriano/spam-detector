# Spam Detector

[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://java.com)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-green.svg)](https://spring.io/projects/spring-boot)
![ML](https://img.shields.io/badge/ML-Random%20Forest-blueviolet)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Ένα έξυπνο σύστημα ανίχνευσης spam μηνυμάτων που υποστηρίζει Ελληνικά και Αγγλικά. Χρησιμοποιεί πραγματικό Machine Learning model (Random Forest) σε συνδυασμό με rule-based filters για ακριβή ανίχνευση spam.
## Λειτουργίες

-  **Πραγματικό ML Model** (Random Forest με Weka)
-  **Πολύγλωσση υποστήριξη** (Ελληνικά & Αγγλικά)
-  **Έξυπνος αλγόριθμος** με συνδυασμό ML και rules
-  **Web Interface** για εύκολη χρήση
-  **Αναλυτικά αποτελέσματα** με confidence scores
-  **Feature extraction** (URLs, keywords, κεφαλαία, κλπ)
-  **Ιστορικό ελέγχων** (με βάση δεδομένων H2)
-  **Σύστημα Feedback** για βελτίωση του αλγορίθμου
-  **Responsive design** για όλες τις συσκευές
-  **Real-time spam detection** με πιθανότητες ML

## Τεχνολογίες

- **Backend:** Java 21, Spring Boot 3.5.8
- **Frontend:** HTML5, CSS3, JavaScript (Vanilla)
- **Database:** H2 (In-memory)
- **ML Engine:** Weka 3.8.6 (Random Forest)
- **Build Tool:** Maven
- **Architecture:** MVC Pattern, REST API

## Γρήγορη Εγκατάσταση

### Προαπαιτούμενα
- Java 21 ή νεότερη
- Maven 3.6+ 

### Βήματα Εγκατάστασης

1. Κλώνος του repository

git clone https://github.com/panagiotis/spam-detector.git
cd spam-detector

2. Build της εφαρμογής

mvn clean compile

3. Εκτέλεση

mvn spring-boot:run

4. Πρόσβαση στην εφαρμογή

http://localhost:8080

##  Δομή Project

spam-detector/
├── src/main/java/com/panagiotis/spam_detector/
│   ├── controller/        # REST Controllers
│   ├── entity/            # JPA Entities
│   ├── repository/        # Data Access Layer
│   ├── service/           # Business Logic
│   ├── ml/                # ML Module (ΝΕΟ)
│   │   ├── SpamClassifier.java
│   │   ├── FeatureExtractor.java
│   │   └── TrainingData.java
│   └── config/            # Configuration Classes
├── src/main/resources/
│   ├── static/            # Frontend Resources
│   └── application.properties
└── pom.xml

## Χρήση

- Εισαγωγή μηνύματος στο κύριο textarea
- Πάτημα "Ελέγχος για Spam" για ανάλυση
- Προβολή αποτελεσμάτων με πιθανότητα spam
- Αξιολόγηση της ανάλυσης μέσω του feedback system

## ML Features
Το σύστημα χρησιμοποιεί 11 features για spam detection:

spam_keyword_count - Λέξεις spam
url_present - URL σε μήνυμα
excessive_caps - Υπερβολικά κεφαλαία
multiple_exclamations - Πολλαπλά θαυμαστικά
phone_pattern - Αριθμοί τηλεφώνων
urgency_words - Λέξεις επείγοντος
short_length - Σύντομα μηνύματα
free_mentions - Λέξεις "δωρεάν"
suspicious_patterns - Ύποπτα patterns
money_mentions - Αναφορές σε χρήματα

spam_ratio - Αναλογία spam λέξεων
## Στατιστικά

- Η εφαρμογή παρέχει:
- Ποσοστό ακρίβειας ανίχνευσης
- Στατιστικά ανά γλώσσα
- Ιστορικό διαφορών συστήματος-χρήστη
- Real-time metrics

## Training Data

Το ML model εκπαιδεύτηκε με 20 παραδείγματα spam/ham μηνυμάτων στα Ελληνικά και Αγγλικά.
