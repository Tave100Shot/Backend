package com.api.TaveShot.domain.newsletter.client.repository;

import com.api.TaveShot.domain.newsletter.client.domain.Subscription;
import com.api.TaveShot.domain.newsletter.domain.LetterType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByMemberIdAndLetterType(Long memberId, LetterType letterType);
    List<Subscription> findByLetterType(LetterType type);
    List<Subscription> findByMemberId(Long memberId);


}

