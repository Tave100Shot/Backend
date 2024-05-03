package com.api.TaveShot.domain.newsletter.client.controller;

import com.api.TaveShot.domain.newsletter.client.dto.SubscriptionRequest;
import com.api.TaveShot.domain.newsletter.client.dto.SubscriptionResponse;
import com.api.TaveShot.domain.newsletter.client.service.SubscriptionService;
import com.api.TaveShot.global.success.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscription")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Operation(summary = "뉴스레터 구독", description = "뉴스레터를 구독합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "뉴스레터 구독 성공",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SubscriptionResponse.class)))
    })
    @PostMapping
    public SuccessResponse<SubscriptionResponse> subscribe(@RequestBody SubscriptionRequest request) {
        SubscriptionResponse response = subscriptionService.subscribe(request);
        return new SuccessResponse<>(response);
    }
}
