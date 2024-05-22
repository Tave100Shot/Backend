package com.api.TaveShot.domain.newsletter.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;


import java.time.LocalDate;

@Schema(description = "행사 수정 요청 데이터")
public record EventUpdateRequest(
        @Schema(description = "수정할 행사 ID", example = "1")
        Long eventId,

        @Schema(description = "새 행사 제목", example = "월간 개발 소식 업데이트")
        String title,

        @Schema(description = "새 행사 내용", example = "업데이트된 내용을 포함합니다...")
        String content,

        @Schema(description = "새 행사 시작 날짜", example = "2024-01-01")
        LocalDate startDate,

        @Schema(description = "새 행사 종료 날짜", example = "2024-01-07")
        LocalDate endDate

) {
}
