package com.api.TaveShot.domain.newsletter.letter.listener;

import com.api.TaveShot.domain.Member.domain.Member;
import com.api.TaveShot.domain.Member.repository.MemberRepository;
import com.api.TaveShot.domain.newsletter.client.service.EmailSenderService;
import com.api.TaveShot.domain.newsletter.domain.EmailSendStatus;
import com.api.TaveShot.domain.newsletter.domain.Newsletter;
import com.api.TaveShot.domain.newsletter.event.NewsletterCreatedEvent;
import com.api.TaveShot.domain.newsletter.repository.EmailSendStatusRepository;
import com.api.TaveShot.global.exception.ApiException;
import com.api.TaveShot.global.exception.ErrorType;
import lombok.RequiredArgsConstructor;
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
    private final EmailSendStatusRepository emailSendStatusRepository;
    private final MemberRepository memberRepository;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNewsletterCreatedEvent(NewsletterCreatedEvent event) {
        Newsletter newsletter = event.getNewsletter();
        List<String> recipientEmails = event.getRecipientEmails();

        recipientEmails.forEach(email -> {
            Member member = memberRepository.findByGitEmail(email).orElse(null);
            EmailSendStatus status = EmailSendStatus.builder()
                    .newsletter(newsletter)
                    .member(member)
                    .email(email)
                    .build();

            try {
                emailSenderService.sendEmail(email, newsletter.getTitle(), event.getContent());
                status.markSuccess();
            } catch (MessagingException e) {
                status.markFailed();
                throw new ApiException(ErrorType._EMAIL_SEND_FAILED_LISTENER);
            } finally {
                emailSendStatusRepository.save(status);
            }
        });
    }
}
