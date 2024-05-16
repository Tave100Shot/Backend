package com.api.TaveShot.domain.newsletter.admin.dto;

import com.api.TaveShot.domain.newsletter.domain.LetterType;
import com.api.TaveShot.domain.newsletter.domain.Newsletter;
import com.api.TaveShot.global.util.TimeUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "개별 뉴스레터의 상세 정보")
public record NewsletterSingleResponse(

        @Schema(description = "뉴스레터 id", example = "2")
        Long newsletterId,

        @Schema(description = "뉴스레터 제목", example = "월간 개발 소식")
        String title,

        @Schema(description = "뉴스레터 내용", example = "이번 달의 주요 개발 관련 소식입니다...")
        String content,

        @Schema(description = "뉴스레터 타입", example = "DEV_LETTER")
        LetterType letterType,

        @Schema(description = "작성 시간", example = "21.08.01 12:00")
        String writtenTime,

        @Schema(description = "전송 성공 여부", example = "true")
        boolean sendStatus
) {

    public static NewsletterSingleResponse from(final Newsletter newsletter) {
        LocalDateTime modifiedDate = newsletter.getLastModifiedDate();
        String writtenTime = TimeUtil.formatCreatedDate(modifiedDate);

        return new NewsletterSingleResponse(
                newsletter.getId(), newsletter.getTitle(),
                newsletter.getContent(), newsletter.getLetterType(),
                writtenTime, newsletter.isSent()
        );
    }
}
