package com.api.TaveShot.domain.news_letter.content.service;

import com.api.TaveShot.domain.news_letter.content.domain.NewsLetterContent;
import com.api.TaveShot.domain.news_letter.content.dto.NewsLetterContentRequestDto;
import com.api.TaveShot.domain.news_letter.content.repository.NewsLetterContentRepository;
import com.api.TaveShot.domain.news_letter.main.domain.SubscriptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewsLetterContentService {

    private final NewsLetterContentRepository newsLetterContentRepository;

    public NewsLetterContent saveContent(NewsLetterContentRequestDto contentRequestDto) {
        SubscriptionType subscriptionType = SubscriptionType.findType(contentRequestDto.getSubscriptionType());
        Integer dayOfMonth = subscriptionType.getAssociatedDays().get(0);

        NewsLetterContent content = NewsLetterContent.builder()
                .subscriptionType(subscriptionType)
                .content(contentRequestDto.getContent())
                .dayOfMonth(dayOfMonth)
                .build();

        return newsLetterContentRepository.save(content);

    }
}

