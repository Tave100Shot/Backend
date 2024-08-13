package com.api.TaveShot.domain.newsletter.letter.dto;

import java.util.List;

public record NewsletterDateResponse(List<NewsletterSingleResponse> newsletterSingleResponses, int year, int month) {

    public static NewsletterDateResponse of(List<NewsletterSingleResponse> newsletterSingleResponses, int year, int month) {
        return new NewsletterDateResponse(newsletterSingleResponses, year, month);
    }

}
