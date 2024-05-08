package com.api.TaveShot.domain.newsletter.client.repository;

import com.api.TaveShot.domain.newsletter.client.domain.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByMemberId(Long memberId);

}

