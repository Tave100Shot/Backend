package com.api.TaveShot.domain.newsletter.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

@Schema(description = "뉴스레터 생성 요청에 필요한 데이터")
public record NewsletterCreateRequest(
        @NotEmpty(message = "뉴스레터 제목은 비워둘 수 없습니다.")
        @Schema(description = "뉴스레터 제목", example = "월간 개발 소식")
        String title,

        @NotEmpty(message = "뉴스레터 내용은 비워둘 수 없습니다.")
        @Schema(description = "뉴스레터 내용", example = "이번 달의 주요 개발 관련 소식입니다...")
        String content,

        @NotEmpty(message = "뉴스레터 타입은 비워둘 수 없습니다.")
        @Schema(description = "뉴스레터 타입", example = "DEV_LETTER")
        String letterType
) {
}