package com.api.TaveShot.domain.newsletter.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDate;

@Schema(description = "행사 생성 요청에 필요한 데이터")
public record EventCreateRequest(
        @NotEmpty(message = "행사 제목은 비워둘 수 없습니다.")
        @Schema(description = "행사 제목", example = "월간 개발 소식")
        String title,

        @NotEmpty(message = "행사 내용은 비워둘 수 없습니다.")
        @Schema(description = "행사 내용", example = "이번 달의 주요 개발 관련 소식입니다...")
        String content,

        @NotEmpty(message = "행사 타입은 비워둘 수 없습니다.")
        @Schema(description = "행사 타입", example = "DEV_LETTER")
        String letterType,

        @NotEmpty(message = "행사 시작 날짜는 비워둘 수 없습니다.")
        @Schema(description = "행사 시작 날짜", example = "2024-01-01")
        LocalDate startDate,

        @NotEmpty(message = "행사 종료 날짜는 비워둘 수 없습니다.")
        @Schema(description = "행사 종료 날짜", example = "2024-01-07")
        LocalDate endDate
) {
}