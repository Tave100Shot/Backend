package com.api.TaveShot.domain.newsletter.admin.controller;

import com.api.TaveShot.domain.newsletter.admin.dto.NewsletterCreateRequest;
import com.api.TaveShot.domain.newsletter.admin.dto.NewsletterPagingResponse;
import com.api.TaveShot.domain.newsletter.admin.dto.NewsletterSingleResponse;
import com.api.TaveShot.domain.newsletter.admin.dto.NewsletterUpdateRequest;
import com.api.TaveShot.domain.newsletter.admin.service.AdminNewsletterService;
import com.api.TaveShot.global.success.SuccessResponse;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/newsletter")
public class AdminNewsletterController {

    private final static int PAGE_SIZE = 10; // 페이지 당 데이터 수

    private final AdminNewsletterService adminNewsletterService;

    @PostMapping
    public SuccessResponse<Long> registerNewsletter(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "뉴스레터 생성 요청 데이터", required = true) @RequestBody NewsletterCreateRequest request) {
        Long newsletterId = adminNewsletterService.register(request);
        return new SuccessResponse<>(newsletterId);
    }


    @GetMapping("/{newsletterId}")
    public SuccessResponse<NewsletterSingleResponse> getSingleNewsletter(@Parameter(description = "조회할 뉴스레터의 ID", required = true, example = "1")
                                                                         @PathVariable final Long newsletterId) {
        return new SuccessResponse<>(adminNewsletterService.findById(newsletterId));
    }

    @GetMapping
    public SuccessResponse<NewsletterPagingResponse> getSingleNewsletter(
            @Parameter(description = "뉴스레터 타입. 유효한 값은 DEV_LETTER, EMPLOYEE_LETTER, ALL 중 하나입니다.", required = true, example = "EMPLOYEE_LETTER")
            @RequestParam final String inputCategory,

            @RequestParam(required = false, defaultValue = "") final String containWord,

            @Parameter(description = "페이지 번호 (0부터 시작, 기본값 0)", example = "0")
            @RequestParam(required = false, defaultValue = "0") final int page
    ) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        return new SuccessResponse<>(adminNewsletterService.getPaging(inputCategory, containWord, pageable));
    }


    @DeleteMapping("/{newsletterId}")
    public SuccessResponse<Long> delete(@Parameter(description = "삭제할 뉴스레터의 ID", required = true, example = "1")
                                        @PathVariable final Long newsletterId) {
        return new SuccessResponse<>(adminNewsletterService.delete(newsletterId));
    }

    @PatchMapping
    public SuccessResponse<Long> edit(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "뉴스레터 수정 요청 데이터", required = true) @RequestBody NewsletterUpdateRequest request) {
        return new SuccessResponse<>(adminNewsletterService.edit(request));
    }
}
