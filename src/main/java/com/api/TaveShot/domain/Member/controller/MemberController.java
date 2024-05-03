package com.api.TaveShot.domain.Member.controller;

import com.api.TaveShot.domain.Member.service.MemberService;
import com.api.TaveShot.global.success.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/updateInfo")
    public SuccessResponse<Long> updateMemberDetails(@RequestParam String gitEmail, @RequestParam String bojName) {
        Long memberId = memberService.updateMemberDetails(gitEmail, bojName);
        return new SuccessResponse<>(memberId);
    }
}
