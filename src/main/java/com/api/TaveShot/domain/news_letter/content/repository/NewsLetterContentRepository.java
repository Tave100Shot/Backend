package com.api.TaveShot.domain.news_letter.content.repository;

import com.api.TaveShot.domain.news_letter.content.domain.NewsLetterContent;
import com.api.TaveShot.domain.news_letter.main.domain.SubscriptionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.time.LocalDate;
import java.util.Optional;

public interface NewsLetterContentRepository extends JpaRepository<NewsLetterContent, Long> {
    List<NewsLetterContent> findBySubscriptionTypeAndDayOfMonth(SubscriptionType subscriptionType, Integer dayOfMonth);

    // 현재 날짜와 구독 유형에 맞는 뉴스레터 내용 조회
    default Optional<NewsLetterContent> findContentForToday(SubscriptionType type) {
        LocalDate today = LocalDate.now();
        int dayOfMonth = today.getDayOfMonth();
        return findBySubscriptionTypeAndDayOfMonth(type, dayOfMonth).stream().findFirst();
    }
}
