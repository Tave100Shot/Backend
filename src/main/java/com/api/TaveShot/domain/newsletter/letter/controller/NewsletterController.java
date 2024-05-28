package com.api.TaveShot.domain.newsletter.letter.controller;


import com.api.TaveShot.domain.newsletter.letter.dto.*;
import com.api.TaveShot.domain.newsletter.letter.service.NewsletterService;
import com.api.TaveShot.global.success.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@RestController
@RequestMapping("/api/newsletter")
@RequiredArgsConstructor
@Tag(name = "Newsletter", description = "뉴스레터 API")
public class NewsletterController {

    private final static int PAGE_SIZE = 10; // 페이지 당 데이터 수

    private final NewsletterService newsletterService;

    @GetMapping("/{newsletterId}")
    public SuccessResponse<NewsletterSingleResponse> getSingleNewsletter(
            @Parameter(description = "조회할 뉴스레터의 ID", required = true, example = "1")
            @PathVariable final Long newsletterId) {
        return new SuccessResponse<>(newsletterService.findById(newsletterId));
    }

    @GetMapping
    public SuccessResponse<NewsletterPagingResponse> getNewsletters(
            @Parameter(description = "뉴스레터 타입. 유효한 값은 DEV_LETTER, EMPLOYEE_LETTER, ALL 중 하나입니다.", required = true, example = "EMPLOYEE_LETTER")
            @RequestParam final String inputCategory,

            @RequestParam(required = false, defaultValue = "") final String containWord,

            @Parameter(description = "페이지 번호 (0부터 시작, 기본값 0)", example = "0")
            @RequestParam(required = false, defaultValue = "0") final int page
    ) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        return new SuccessResponse<>(newsletterService.getPaging(inputCategory, containWord, pageable));
    }

    @GetMapping("/recent")
    public SuccessResponse<NewsletterRecentResponse> getRecentNewsletter() {
        return new SuccessResponse<>(newsletterService.getRecent());
    }

    @GetMapping("/byDate")
    public SuccessResponse<NewsletterDateResponse> getNewsletterByDate(
            @Parameter(description = "요청할 년도", required = false, example = "2024")
            @RequestParam(required = false) final Integer year,

            @Parameter(description = "요청할 월", required = false, example = "5")
            @RequestParam(required = false) final Integer month
    ) {
        if (year == null || month == null) {
            LocalDate now = LocalDate.now();
            int currentYear = now.getYear();
            int currentMonth = now.getMonthValue();

            return new SuccessResponse<>(newsletterService.getNewsletterByDate(currentYear, currentMonth));
        }

        return new SuccessResponse<>(newsletterService.getNewsletterByDate(year, month));
    }
}

