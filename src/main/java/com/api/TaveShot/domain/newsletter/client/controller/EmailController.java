package com.api.TaveShot.domain.newsletter.client.controller;

import com.api.TaveShot.domain.newsletter.client.service.EmailService;
import com.api.TaveShot.global.success.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping
public class EmailController {

    private final EmailService emailService;

    @Operation(summary = "이메일 인증", description = "사용자의 이메일 인증을 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 인증 성공",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Boolean.class)))
    })
    @GetMapping("/verify-email")
    public SuccessResponse<Boolean> verifyEmail(@Valid @RequestParam String token) {
        boolean result = emailService.verifyEmail(token);
        return new SuccessResponse<>(result);
    }
}
