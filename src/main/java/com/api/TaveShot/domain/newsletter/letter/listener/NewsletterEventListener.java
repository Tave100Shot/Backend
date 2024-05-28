package com.api.TaveShot.domain.newsletter.letter.listener;

import com.api.TaveShot.domain.newsletter.client.service.EmailSenderService;
import com.api.TaveShot.domain.newsletter.event.NewsletterCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import jakarta.mail.MessagingException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NewsletterEventListener {
    private final EmailSenderService emailSenderService;

    @Async
    @EventListener
    public void handleNewsletterCreatedEvent(NewsletterCreatedEvent event) throws MessagingException {
        String content = event.getContent();
        List<String> recipientEmails = event.getRecipientEmails();

        for (String email : recipientEmails) {
            emailSenderService.sendEmail(email, event.getNewsletter().getTitle(), content);
        }
    }
}
