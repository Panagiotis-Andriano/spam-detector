package com.panagiotis.spam_detector.entity;

import java.time.LocalDateTime;

import ch.qos.logback.core.util.FixedDelay;
import jakarta.persistence.*;

/**
 * Entity για αποθήκευση feedback από χρήστες σχετικά με την ακρίβεια
 */
@Entity
@Table(name = "feedback")
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "spam_check_id") 
    private Long spamCheckId;

    @Column(nullable = false)
    private boolean userSaysIsSpam;

    @Column(nullable = false)
    private boolean systemSaysIsSpam;

    @Column(length = 500)
    private String userComment;

    @Column(nullable = false) 
    private LocalDateTime createdAt;

    private String messageLanguage;
    private Double systemConfidence;

    public Feedback() {}

    public Feedback(Long spamCheckId, boolean userSaysIsSpam, boolean systemSaysIsSpam, String userComment, String messageLanguage, Double systemConfidence) {
        this.spamCheckId = spamCheckId;
        this.userSaysIsSpam = userSaysIsSpam;
        this.systemSaysIsSpam = systemSaysIsSpam;
        this.userComment = userComment;
        this.messageLanguage = messageLanguage;
        this.systemConfidence = systemConfidence;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSpamCheckId() { return spamCheckId; }
    public void setSpamCheckId(Long spamCheckId) { this.spamCheckId = spamCheckId; }

    public boolean isUserSaysIsSpam() { return userSaysIsSpam; }
    public void setUserSaysIsSpam(boolean userSaysIsSpam) { this.userSaysIsSpam = userSaysIsSpam; }

    public boolean isSystemSaysIsSpam() { return systemSaysIsSpam; }
    public void setSystemSaysIsSpam(boolean systemSaysIsSpam) { this.systemSaysIsSpam = systemSaysIsSpam; }

    public String getUserComment() { return userComment; }
    public void setUserComment(String userComment) { this.userComment = userComment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getMessageLanguage() { return messageLanguage; }
    public void setMessageLanguage(String messageLanguage) { this.messageLanguage = messageLanguage; }

    public Double getSystemConfidence() { return systemConfidence; }
    public void setSystemConfidence(Double systemConfidence) { this.systemConfidence = systemConfidence; }

    public boolean hasDiscrepancy() {
        return userSaysIsSpam != systemSaysIsSpam;
    }
}
