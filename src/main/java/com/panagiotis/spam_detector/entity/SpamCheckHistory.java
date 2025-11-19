package com.panagiotis.spam_detector.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

/**
 * Entity κλάση για τον έλεγχο spam στη βάση δεδομενών
 * Κάθε φορά που πραγματοποιείται ο έλεγχος μηνύματος, δημιουργείται νέα εγγραφή
 */

@Entity
@Table(name = "spam_check_history")
public class SpamCheckHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String message;

    @Column(nullable = false)
    private boolean isSpam;

    @Column(nullable = false)
    private double confidence;
    private String language;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    public SpamCheckHistory() {}

    public SpamCheckHistory(String message, boolean isSpam, double confidence, String language) {
        this.message = message;
        this.isSpam = isSpam;
        this.confidence = confidence;
        this.language = language;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isSpam() { return isSpam; }
    public void setSpam(boolean isSpam) { this.isSpam = isSpam; }

    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }

    public String getLanguage() { return language; }
    public void setLanguage( String language) { this.language = language; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}


