package com.api.TaveShot.domain.news_letter.repository;

import com.api.TaveShot.domain.news_letter.domain.NewsLetter;
import com.api.TaveShot.domain.news_letter.domain.SubscriptionType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface NewsLetterRepository extends JpaRepository<NewsLetter, Long> {
    Optional<NewsLetter> findByEmailAndSubscriptionType(String email, SubscriptionType subscriptionType);
}
