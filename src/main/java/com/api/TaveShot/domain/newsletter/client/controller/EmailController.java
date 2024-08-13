package com.api.TaveShot.domain.newsletter.client.controller;


import com.api.TaveShot.domain.newsletter.client.service.EmailService;
import com.api.TaveShot.domain.newsletter.client.service.EmailTokenService;
import com.api.TaveShot.global.success.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/email")
public class EmailController {

    private final EmailTokenService emailTokenService;
    private final EmailService emailService;

    @PostMapping("/send-verification")
    public SuccessResponse<String> sendVerificationEmail() {
        String tokenId = emailTokenService.createEmailToken();
        String verificationUrl = "http://localhost:3000/api/email/verify?token=" + tokenId;
        return new SuccessResponse<>(verificationUrl);
        //return new SuccessResponse<>(tokenId);
    }

    @Operation(summary = "이메일 인증", description = "사용자의 이메일 인증을 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 인증 성공",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Boolean.class)))
    })
    @GetMapping("/verify")
    public SuccessResponse<Long> verifyEmail(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return new SuccessResponse<>(emailService.verifyEmail(token));
    }
}
