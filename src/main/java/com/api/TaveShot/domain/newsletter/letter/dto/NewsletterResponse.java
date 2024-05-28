package com.api.TaveShot.domain.newsletter.letter.dto;

import com.api.TaveShot.domain.newsletter.domain.LetterType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(description = "주간 뉴스레터 생성 응답")
public record NewsletterResponse(
        @Schema(description = "뉴스레터 ID 맵")
        Map<LetterType, Long> newsletterIds
) {
}
