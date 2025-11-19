package com.panagiotis.spam_detector.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.panagiotis.spam_detector.entity.SpamCheckHistory;

/**
 * Repository interface για λειτουργίες της βάσης δεδομένων στον πίνακα SpamCheckHistory
 * Το Spring Data JPA δημιουργεί αυτόματα τις υλοποιήσεις των μεθόδων
 */

 @Repository
 public interface SpamCheckHistoryRepository extends JpaRepository<SpamCheckHistory, Long> {

    //Βρίσκει το ιστορικό ταξινομημένο από το πιο νέο προς το πιο παλιό
    List<SpamCheckHistory> findByOrderByCreatedAtDesc();

    //Βρίσκει εγγραφές βάση spam status, ταξινομημένες από το πιο νέο προς το πιο παλιό
    List<SpamCheckHistory> findByIsSpamOrderByCreatedAtDesc(boolean isSpam);

    //Μετρητής εγγραφών για συγκεκριμένο spam status
    long countByIsSpam(boolean isSpam);
 }