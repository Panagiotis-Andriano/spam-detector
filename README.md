# Spam Detector

[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://java.com)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-green.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Ένα έξυπνο σύστημα ανίχνευσης spam μηνυμάτων που υποστηρίζει Ελληνικά και Αγγλικά. Χρησιμοποιεί machine learning τεχνικές και rule-based filters για ακριβή ανίχνευση spam.

## Λειτουργίες

-  **Πολύγλωσση υποστήριξη** (Ελληνικά & Αγγλικά)
-  **Έξυπνος αλγόριθμος** με συνδυασμό ML και rules
-  **Web Interface** για εύκολη χρήση
-  **Αναλυτικά αποτελέσματα** με confidence scores
-  **Feature extraction** (URLs, keywords, κεφαλαία, κλπ)
-  **Ιστορικό ελέγχων** (με βάση δεδομένων H2)

## Τεχνολογίες

- **Backend:** Java 21, Spring Boot 3.5.7
- **Frontend:** HTML5, CSS3, JavaScript
- **Database:** H2 (In-memory)
- **Build Tool:** Maven
- **Architecture:** MVC Pattern

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
