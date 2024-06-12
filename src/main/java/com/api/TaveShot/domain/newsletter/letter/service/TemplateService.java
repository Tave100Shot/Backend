package com.api.TaveShot.domain.newsletter.letter.service;


import com.api.TaveShot.domain.newsletter.admin.dto.EventSingleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private final TemplateEngine templateEngine;

    public String renderHtmlContent(List<EventSingleResponse> events, String title, String template) {
        Context context = new Context();
        context.setVariable("title", title);
        context.setVariable("events", events);

        return templateEngine.process(template, context);
    }
}
