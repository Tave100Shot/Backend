package com.api.TaveShot.domain.newsletter.client.repository;

import com.api.TaveShot.domain.newsletter.client.domain.Subscription;
import com.api.TaveShot.domain.newsletter.domain.LetterType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByMemberId(Long memberId);

    @Query("SELECT s FROM Subscription s WHERE s.letterType = :letterType OR s.letterType = 'ALL'")
    List<Subscription> findAllByLetterType(@Param("letterType") LetterType letterType);
}
