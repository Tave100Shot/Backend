package com.api.TaveShot.domain.Member.controller;

import com.api.TaveShot.domain.Member.domain.Member;
import com.api.TaveShot.domain.Member.dto.request.MemberUpdateInfo;
import com.api.TaveShot.domain.Member.dto.response.MemberInfoResponse;
import com.api.TaveShot.domain.Member.dto.response.MemberPagingResponse;
import com.api.TaveShot.domain.Member.service.MemberService;
import com.api.TaveShot.global.success.SuccessResponse;
import com.api.TaveShot.global.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberController {

    private static final Integer PAGE_SIZE = 10;

    private final MemberService memberService;

    @Operation(summary = "사용자 gitEmail, bojName 정보 수정", description = "사용자의 gitEmail, bojName 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "gitEmail, bojName 정보 수정 성공",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Long.class)))
    })
    @PostMapping("/member")
    public SuccessResponse<Long> updateMemberDetails(@RequestBody MemberUpdateInfo updateInfo) {
        Long memberId = memberService.updateMemberDetails(updateInfo);
        return new SuccessResponse<>(memberId);
    }

    @GetMapping("/member/info")
    public SuccessResponse<MemberInfoResponse> getMemberInfo() {
        Member currentMember = SecurityUtil.getCurrentMember();
        MemberInfoResponse response = memberService.getMemberInfo(currentMember.getId());
        return new SuccessResponse<>(response);
    }

    @Operation(
            summary = "멤버 목록 페이징 조회",
            description = "회원의 백준 닉네임과 이메일로 검색할 수 있습니다. 멤버 목록을 페이징하여 조회합니다."
    )
    @GetMapping("/admin/members")
    public SuccessResponse<MemberPagingResponse> getMemberListPaging(
            @Parameter(description = "백준 닉네임", example = "John Doe")
            @RequestParam(required = false, defaultValue = "") final String memberName,

            @Parameter(description = "멤버 이메일", example = "john.doe@example.com")
            @RequestParam(required = false, defaultValue = "") final String memberEmail,

            @Parameter(description = "페이지 번호 (0부터 시작, 기본값 0)", example = "0")
            @RequestParam(required = false, defaultValue = "0") final int page
    ) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        return new SuccessResponse<>(
                memberService.getMemberPaging(memberName, memberEmail, pageable)
        );
    }
}
