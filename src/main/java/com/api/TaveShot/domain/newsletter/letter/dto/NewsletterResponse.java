package com.api.TaveShot.domain.newsletter.letter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "주간 뉴스레터 생성 응답")
public record NewsletterResponse(
        @Schema(description = "개발자 뉴스레터 ID")
        Long devNewsletterId,

        @Schema(description = "직원 뉴스레터 ID")
        Long employeeNewsletterId
) {
}
