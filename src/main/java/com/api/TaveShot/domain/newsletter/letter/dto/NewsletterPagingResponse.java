package com.api.TaveShot.domain.newsletter.letter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "페이징 처리된 뉴스레터 리스트와 페이지 관련 정보")
public record NewsletterPagingResponse(
        @Schema(description = "페이지에 해당하는 뉴스레터 리스트")
        List<NewsletterSingleResponse> newsletterResponses,

        @Schema(description = "전체 페이지 수")
        Integer totalPage,

        @Schema(description = "전체 뉴스레터 수")
        Long totalElements,

        @Schema(description = "현재 페이지가 첫 페이지인지 여부")
        Boolean isFirst,

        @Schema(description = "현재 페이지가 마지막 페이지인지 여부")
        Boolean isLast
) {
    public static NewsletterPagingResponse of(List<NewsletterSingleResponse> singleResponses, Integer totalPage,
                                              Long totalElements, Boolean isFirst, Boolean isLast) {
        return new NewsletterPagingResponse(singleResponses, totalPage, totalElements, isFirst, isLast);
    }
}