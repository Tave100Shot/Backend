package com.api.TaveShot.domain.news_letter.main.service;

import com.api.TaveShot.domain.news_letter.content.repository.NewsLetterContentRepository;
import com.api.TaveShot.domain.news_letter.main.domain.NewsLetter;
import com.api.TaveShot.domain.news_letter.main.domain.SubscriptionType;
import com.api.TaveShot.domain.news_letter.main.dto.NewsLetterRequestDto;
import com.api.TaveShot.domain.news_letter.main.repository.NewsLetterRepository;
import com.api.TaveShot.global.exception.ApiException;
import com.api.TaveShot.global.exception.ErrorType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NewsLetterService {
    private final NewsLetterRepository newsLetterRepository;
    private final NewsLetterContentRepository newsLetterContentRepository;
    private final EmailService emailService;

    public NewsLetterService(NewsLetterRepository newsLetterRepository, NewsLetterContentRepository newsLetterContentRepository, EmailService emailService) {
        this.newsLetterRepository = newsLetterRepository;
        this.newsLetterContentRepository = newsLetterContentRepository;
        this.emailService = emailService;
    }


    public Long subscribe(NewsLetterRequestDto requestDto) {
        Optional<NewsLetter> existingSubscription = newsLetterRepository
                .findByEmailAndSubscriptionType(requestDto.getEmail(), requestDto.getSubscriptionType());

        if (existingSubscription.isPresent()) {
            throw new ApiException(ErrorType._SUBSCRIPTION_ALREADY_EXIST);
        }

        NewsLetter newsLetter = newsLetterRepository.save(NewsLetter.builder()
                .nickname(requestDto.getNickname())
                .email(requestDto.getEmail())
                .subscriptionType(requestDto.getSubscriptionType())
                .build());
        return newsLetter.getId();
    }

    @Scheduled(cron = "0 0 9 10 * ?") // 매월 10일 9시에 발송
    public void sendTechnologyNewsLetters() {
        newsLetterContentRepository.findContentForToday(SubscriptionType.TECHNOLOGY_KNOWLEDGE).ifPresent(content -> {
            List<NewsLetter> subscribers = newsLetterRepository.findBySubscriptionType(SubscriptionType.TECHNOLOGY_KNOWLEDGE);
            for (NewsLetter subscriber : subscribers) {
                emailService.sendEmail(subscriber.getEmail(), "DEV LETTER", content.getContent());
            }
        });
    }

    @Scheduled(cron = "0 0 9 15 * ?") // 매월 15일 9시에 발송
    public void sendJobNewsLetters() {
        newsLetterContentRepository.findContentForToday(SubscriptionType.JOB_INFORMATION).ifPresent(content -> {
            List<NewsLetter> subscribers = newsLetterRepository.findBySubscriptionType(SubscriptionType.JOB_INFORMATION);
            for (NewsLetter subscriber : subscribers) {
                emailService.sendEmail(subscriber.getEmail(), "EMPLOY LETTER", content.getContent());
            }
        });
    }

}
