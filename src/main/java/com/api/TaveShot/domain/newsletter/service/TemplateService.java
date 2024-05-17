package com.api.TaveShot.domain.newsletter.service;

import com.api.TaveShot.domain.newsletter.admin.dto.NewsletterCreateRequest;
import com.api.TaveShot.domain.newsletter.domain.LetterType;
import com.api.TaveShot.global.exception.ApiException;
import com.api.TaveShot.global.exception.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private final TemplateEngine templateEngine;

    public String renderHtmlContent(NewsletterCreateRequest request, LetterType letterType) {
        Context context = new Context();
        context.setVariable("title", request.letterType() + " - " + request.title());
        context.setVariable("content", request.content());

        String templateName = getTemplateNameByLetterType(letterType);
        return templateEngine.process(templateName, context);
    }

    public String getTemplateNameByLetterType(LetterType letterType) {
        switch (letterType) {
            case DEV_LETTER:
                return "dev_newsletter";
            case EMPLOYEE_LETTER:
                return "employee_newsletter";
            default:
                throw new IllegalArgumentException("Unsupported LetterType: " + letterType);
        }
    }

    public String loadHtmlTemplate(String fileName) throws IOException {
        ClassPathResource resource = new ClassPathResource(fileName);
        try (InputStream inputStream = resource.getInputStream()) {
            byte[] bytes = inputStream.readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }

    public String getHtmlContent(String templateName, NewsletterCreateRequest request) {
        String htmlTemplate;
        try {
            htmlTemplate = loadHtmlTemplate("templates/" + templateName + ".html");
        } catch (IOException e) {
            throw new ApiException(ErrorType._TEMPLATE_READ_FAILED);
        }

        htmlTemplate = htmlTemplate.replace("${title}", request.title());
        htmlTemplate = htmlTemplate.replace("${content}", request.content());

        return htmlTemplate;
    }
}
