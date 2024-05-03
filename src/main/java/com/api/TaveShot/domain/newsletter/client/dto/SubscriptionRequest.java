package com.api.TaveShot.domain.newsletter.client.dto;

import com.api.TaveShot.domain.newsletter.domain.LetterType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SubscriptionRequest {

    @Schema(description = "구독 유형", example = "'DEV_LETTER' or 'EMPLOYEE_LETTER'")
    @NotEmpty
    private LetterType letterType;
}
