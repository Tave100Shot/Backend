package com.api.TaveShot.domain.newsletter.service;

import com.api.TaveShot.domain.newsletter.admin.dto.NewsletterCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private final SpringTemplateEngine templateEngine;

    public String renderHtmlContent(NewsletterCreateRequest request) {
        Context context = new Context();
        context.setVariable("title", request.letterType() + " - " + request.title());
        context.setVariable("content", request.content());
        return templateEngine.process("newsletter", context);
    }
}

