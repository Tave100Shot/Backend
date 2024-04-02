package com.api.TaveShot.domain.news_letter.content.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class NewsLetterContentRequestDto {

    @Schema(description = "구독 유형", example = "'Technology' or 'Job'")
    @NotEmpty
    private String subscriptionType;

    @Schema(description = "뉴스레터 내용", example = "뉴스레터 내용 예시")
    @NotEmpty
    private String content;

}
