package com.api.TaveShot.domain.Member.controller;

import com.api.TaveShot.domain.Member.service.MemberService;
import com.api.TaveShot.global.success.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @Operation(summary = "사용자 gitEmail, bojName 정보 수정", description = "사용자의 gitEmail, bojName 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "gitEmail, bojName 정보 수정 성공",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Long.class)))
    })
    @PostMapping("/updateInfo")
    public SuccessResponse<Long> updateMemberDetails(@RequestParam String gitEmail, @RequestParam String bojName) {
        Long memberId = memberService.updateMemberDetails(gitEmail, bojName);
        return new SuccessResponse<>(memberId);
    }
}
