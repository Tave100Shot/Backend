package com.api.TaveShot.domain.newsletter.client.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSenderService {
    private final JavaMailSender mailSender;

    @Async
    public void sendEmail(SimpleMailMessage email) {
        mailSender.send(email);
    }
}
