package com.api.TaveShot.domain.news_letter.main.repository;

import com.api.TaveShot.domain.news_letter.main.domain.NewsLetter;
import com.api.TaveShot.domain.news_letter.main.domain.SubscriptionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NewsLetterRepository extends JpaRepository<NewsLetter, Long> {
    Optional<NewsLetter> findByEmailAndSubscriptionType(String email, SubscriptionType subscriptionType);
    List<NewsLetter> findBySubscriptionType(SubscriptionType subscriptionType);
}
