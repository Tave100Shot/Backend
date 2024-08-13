package com.api.TaveShot.domain.newsletter.letter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "최근 6개 뉴스레터")
public record NewsletterRecentResponse(List<NewsletterSingleResponse> newsletterSingleResponses) {

    public static NewsletterRecentResponse from(List<NewsletterSingleResponse> newsletterSingleResponses) {
        return new NewsletterRecentResponse(newsletterSingleResponses);
    }
}
