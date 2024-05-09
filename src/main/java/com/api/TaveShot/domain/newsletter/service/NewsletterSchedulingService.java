package com.api.TaveShot.domain.newsletter.service;

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

import java.util.*;

@Service
@RequiredArgsConstructor
public class NewsletterSchedulingService {

    private final SubscriptionRepository subscriptionRepository;
    private final NewsletterRepository newsletterRepository;
    private final EmailSenderService emailSenderService;

    @Scheduled(initialDelay = 60000, fixedDelay = 5000)
    public void sendNewsletters() {
        List<Subscription> subscriptions = subscriptionRepository.findAll();
        Map<LetterType, Newsletter> latestNewslettersByType = fetchLatestNewslettersByType();

        for (Subscription sub : subscriptions) {
            List<LetterType> typesToSend = determineTypesToSend(sub.getLetterType());

            for (LetterType type : typesToSend) {
                Newsletter newsletterToSend = latestNewslettersByType.get(type);
                if (newsletterToSend != null && !newsletterToSend.isSent()) {
                    try {
                        emailSenderService.sendEmail(
                                sub.getMember().getGitEmail(),
                                newsletterToSend.getTitle(),
                                newsletterToSend.getContent()
                        );
                        newsletterToSend.letterSent();
                        newsletterRepository.save(newsletterToSend);
                    } catch (MessagingException e) {
                        System.err.println("Failed to send email to: " + sub.getMember().getGitEmail() + "; Error: " + e.getMessage());
                        throw new ApiException(ErrorType._EMAIL_SEND_FAILED);
                    }
                }
            }
        }
    }

    private Map<LetterType, Newsletter> fetchLatestNewslettersByType() {
        Map<LetterType, Newsletter> latestNewslettersByType = new EnumMap<>(LetterType.class);
        for (LetterType type : LetterType.values()) {
            newsletterRepository.findTop1ByLetterTypeAndSentOrderByCreatedDateAsc(type, false)
                    .ifPresent(newsletter -> latestNewslettersByType.put(type, newsletter));
        }
        return latestNewslettersByType;
    }

    private List<LetterType> determineTypesToSend(LetterType subscribedType) {
        if (subscribedType == LetterType.ALL) {
            return Arrays.asList(LetterType.DEV_LETTER, LetterType.EMPLOYEE_LETTER);
        } else {
            return Collections.singletonList(subscribedType);
        }
    }
}

