package com.api.TaveShot.domain.newsletter.letter.listener;

import com.api.TaveShot.domain.newsletter.client.service.EmailSenderService;
import com.api.TaveShot.domain.newsletter.event.NewsletterCreatedEvent;
import com.api.TaveShot.global.exception.ApiException;
import com.api.TaveShot.global.exception.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import jakarta.mail.MessagingException;

import java.util.List;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NewsletterEventListener {
    private final EmailSenderService emailSenderService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNewsletterCreatedEvent(NewsletterCreatedEvent event) {
        String content = event.getContent();
        List<String> recipientEmails = event.getRecipientEmails();

        recipientEmails
                .forEach(email -> {
                    try {
                        emailSenderService.sendEmail(email, event.getNewsletter().getTitle(), content);
                    } catch (MessagingException e) {
                        throw new ApiException(ErrorType._EMAIL_SEND_FAILED_LISTENER);
                    } catch (Exception e) {
                        throw new ApiException(ErrorType._STATIC_ERROR_RUNTIME_EXCEPTION);
                    }
                });
    }
}
