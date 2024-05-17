package com.api.TaveShot.domain.newsletter.client.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SubscriptionResponse {

    @Schema(description = "구독 유형")
    private String letterType;

    @Schema(description = "사용자 gitEmail")
    private String gitEmail;

    @Schema(description = "사용자 bojName")
    private String bojName;
}
