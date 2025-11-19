package com.panagiotis.spam_detector.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.panagiotis.spam_detector.entity.Feedback;

/**
 * Repository interface για λειτουργίες της βάσης δεδομένων στον πίνακα Feedback
 */
public interface FeedbackRepository extends JpaRepository<Feedback, Long> { 
    
    //Βρίσκει όλα τα feedback ταξινομημένο από το πιο νέο προς το πιο παλιό
    List<Feedback> findByOrderByCreatedAtDesc();

    //Βρίσκει feedback για συγκεκριμένο spam
    List<Feedback> findBySpamCheckId(Long spamCheckId);

    //Βρίσκει feedback που έχουν discrepancy μεταξύ συστήματος και χρήστη
    //@Query("SELECT f FROM Feddback f WHERE f.userSaysIsSpam != f.systemSaysIsSpam")
    //List<Feedback> findByUserSaysIsSpamNot(boolean systemSaysIsSpam);

    long count();

    //Μετρητής feddback που έχουν discrepancy
    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.userSaysIsSpam != f.systemSaysIsSpam")
    long countDiscrepancies();

    //Μέτραει τα feedback ανά γλώσσα
    @Query("SELECT f.messageLanguage, COUNT(f) FROM Feedback f GROUP BY f.messageLanguage")
    List<Object[]> countByLanguage();

    //Βρίσκει όλα τα feedback με discrepancies
    @Query("SELECT f FROM Feedback f WHERE f.userSaysIsSpam != f.systemSaysIsSpam ORDER BY f.createdAt DESC")
    List<Feedback> findDiscrepancies();
}
