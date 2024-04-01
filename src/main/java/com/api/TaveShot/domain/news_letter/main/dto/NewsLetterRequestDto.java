package com.api.TaveShot.domain.news_letter.main.dto;

import com.api.TaveShot.domain.news_letter.main.domain.SubscriptionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class NewsLetterRequestDto {

    @Schema(description = "구독자 닉네임", example = "구독자 닉네임 예시")
    @NotEmpty
    private String nickname;

    @Schema(description = "구독자 이메일", example = "구독자 이메일 예시")
    @NotEmpty
    private String email;

    @Schema(description = "구독 유형", example = "'Technology' or 'Job'")
    @NotEmpty
    private String subscriptionType;

    public SubscriptionType getSubscriptionType() {
        // 문자열을 enum으로 변환
        return SubscriptionType.findType(subscriptionType);
    }
}
