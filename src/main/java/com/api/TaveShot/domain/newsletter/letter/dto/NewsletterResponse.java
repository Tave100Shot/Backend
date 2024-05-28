package com.api.TaveShot.domain.newsletter.letter.dto;

import com.api.TaveShot.domain.newsletter.domain.Newsletter;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "주간 뉴스레터 생성 응답")
public record NewsletterResponse(
        Newsletter devNewsletter,
        Newsletter employeeNewsletter
) {
}
