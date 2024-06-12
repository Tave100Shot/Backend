package com.api.TaveShot.domain.newsletter.event;

import com.api.TaveShot.domain.newsletter.domain.Newsletter;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class NewsletterCreatedEvent {
    private final Newsletter newsletter;
    private final String content;
    private final List<String> recipientEmails;
}