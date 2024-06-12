package com.api.TaveShot.domain.newsletter.letter.dto;

import com.api.TaveShot.domain.newsletter.domain.LetterType;
import com.api.TaveShot.domain.newsletter.domain.Newsletter;
import com.api.TaveShot.global.util.TimeUtil;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "개별 뉴스레터의 상세 정보")
public record NewsletterSingleResponse(
        @Schema(description = "뉴스레터 ID", example = "1")
        Long newsletterId,

        @Schema(description = "뉴스레터 제목", example = "Weekly Newsletter")
        String title,

        @Schema(description = "뉴스레터 내용", example = "This is the weekly newsletter content.")
        String content,

        @Schema(description = "뉴스레터 타입", example = "DEV_LETTER")
        LetterType letterType,

        @Schema(description = "작성 시간", example = "21.08.01 12:00")
        String writtenTime
) {
    public static NewsletterSingleResponse from(final Newsletter newsletter) {
        LocalDateTime modifiedDate = newsletter.getLastModifiedDate();
        String writtenTime = TimeUtil.formatNewsletter(modifiedDate);

        return new NewsletterSingleResponse(
                newsletter.getId(), newsletter.getTitle(),
                newsletter.getContent(), newsletter.getLetterType(),
                writtenTime
        );
    }
}
