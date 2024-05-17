package com.api.TaveShot.domain.newsletter.service;

import com.api.TaveShot.domain.newsletter.admin.dto.NewsletterCreateRequest;
import com.api.TaveShot.domain.newsletter.client.domain.Subscription;
import com.api.TaveShot.domain.newsletter.domain.LetterType;
import com.api.TaveShot.domain.newsletter.domain.Newsletter;
import com.api.TaveShot.domain.newsletter.repository.NewsletterRepository;
import com.api.TaveShot.domain.newsletter.client.repository.SubscriptionRepository;
import com.api.TaveShot.domain.newsletter.client.service.EmailSenderService;
import com.api.TaveShot.global.exception.ApiException;
import com.api.TaveShot.global.exception.ErrorType;
import jakarta.mail.MessagingException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class NewsletterSchedulingService {

    private final SubscriptionRepository subscriptionRepository;
    private final NewsletterRepository newsletterRepository;
    private final EmailSenderService emailSenderService;
    private final TemplateService templateService;

    @Transactional
    //@Scheduled(initialDelay = 60000, fixedDelay = 86400000) // 테스트용 스케줄러
    //@Scheduled(cron = "0 40 12 ? * FRI")// 테스트용 스케줄러
    // 격주 월요일 오전 8시에 전송
    @Scheduled(cron = "0 0 8 ? * MON/2")
    public void sendNewsletters() {
        List<Subscription> subscriptions = subscriptionRepository.findAll();
        Map<LetterType, Newsletter> latestNewslettersByType = fetchLatestNewslettersByType();

        for (LetterType type : latestNewslettersByType.keySet()) {
            Newsletter newsletterToSend = latestNewslettersByType.get(type);
            if (newsletterToSend != null && !newsletterToSend.isSent()) {
                boolean allEmailsSent = sendEmailsToAllSubscribers(subscriptions, newsletterToSend);
                if (allEmailsSent) {
                    newsletterToSend.letterSent();
                    newsletterRepository.save(newsletterToSend);
                }
            }
        }
    }

    private boolean sendEmailsToAllSubscribers(List<Subscription> subscriptions, Newsletter newsletterToSend) {
        boolean allEmailsSent = true;
        for (Subscription sub : subscriptions) {
            if (canSend(sub.getLetterType(), newsletterToSend.getLetterType())) {
                try {
                    sendEmailForNewsletter(sub, newsletterToSend);
                } catch (MessagingException e) {
                    System.err.println("Failed to send email to: " + sub.getMember().getGitEmail() + "; Error: " + e.getMessage());
                    allEmailsSent = false;
                }
            }
        }
        return allEmailsSent;
    }

    private Map<LetterType, Newsletter> fetchLatestNewslettersByType() {
        Map<LetterType, Newsletter> latestNewslettersByType = new EnumMap<>(LetterType.class);
        for (LetterType type : LetterType.values()) {
            newsletterRepository.findTop1ByLetterTypeAndSentOrderByCreatedDateAsc(type, false)
                    .ifPresent(newsletter -> latestNewslettersByType.put(type, newsletter));
        }
        return latestNewslettersByType;
    }

    private boolean canSend(LetterType subscribedType, LetterType newsletterType) {
        if (subscribedType == LetterType.ALL) {
            return newsletterType == LetterType.DEV_LETTER || newsletterType == LetterType.EMPLOYEE_LETTER;
        }
        return subscribedType == newsletterType;
    }

    private void sendEmailForNewsletter(Subscription sub, Newsletter newsletterToSend) throws MessagingException {
        NewsletterCreateRequest request = new NewsletterCreateRequest(newsletterToSend.getTitle(), newsletterToSend.getContent(), newsletterToSend.getLetterType().name());
        String htmlContent = templateService.getHtmlContent(templateService.getTemplateNameByLetterType(newsletterToSend.getLetterType()), request);
        String recipientEmail = sub.getMember().getGitEmail();
        emailSenderService.sendEmail(recipientEmail, newsletterToSend.getTitle(), htmlContent);
    }
}
