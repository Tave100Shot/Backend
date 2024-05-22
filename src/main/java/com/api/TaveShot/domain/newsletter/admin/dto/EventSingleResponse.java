package com.api.TaveShot.domain.newsletter.admin.dto;

import com.api.TaveShot.domain.newsletter.domain.Event;
import com.api.TaveShot.domain.newsletter.domain.LetterType;
import com.api.TaveShot.global.util.TimeUtil;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "개별 행사의 상세 정보")
public record EventSingleResponse(

        @Schema(description = "행사 id", example = "2")
        Long eventId,

        @Schema(description = "행사 제목", example = "월간 개발 소식")
        String title,

        @Schema(description = "행사 내용", example = "이번 달의 주요 개발 관련 소식입니다...")
        String content,

        @Schema(description = "행사 타입", example = "DEV_LETTER")
        LetterType letterType,

        @Schema(description = "행사 시작 날짜", example = "2024-01-01")
        LocalDate startDate,

        @Schema(description = "행사 종료 날짜", example = "2024-01-07")
        LocalDate endDate,

        @Schema(description = "작성 시간", example = "21.08.01 12:00")
        String writtenTime
) {
    public static EventSingleResponse from(final Event event) {
        LocalDateTime modifiedDate = event.getLastModifiedDate();
        String writtenTime = (modifiedDate != null) ? TimeUtil.formatNewsletter(modifiedDate) : "Unknown";

        return new EventSingleResponse(
                event.getId(), event.getTitle(),
                event.getContent(), event.getLetterType(),
                event.getStartDate(), event.getEndDate(),
                writtenTime
        );
    }
}
