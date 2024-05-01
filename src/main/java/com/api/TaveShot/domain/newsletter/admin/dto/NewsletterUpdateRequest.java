package com.api.TaveShot.domain.newsletter.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "뉴스레터 수정 요청 데이터")
public record NewsletterUpdateRequest(
        @Schema(description = "수정할 뉴스레터 ID", example = "1")
        Long newsletterId,

        @Schema(description = "새 뉴스레터 제목", example = "월간 개발 소식 업데이트")
        String title,

        @Schema(description = "새 뉴스레터 내용", example = "업데이트된 내용을 포함합니다...")
        String content
) {
}
