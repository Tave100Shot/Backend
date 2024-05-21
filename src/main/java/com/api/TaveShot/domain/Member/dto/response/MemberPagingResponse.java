package com.api.TaveShot.domain.Member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "페이징 처리된 회원 정보")
public record MemberPagingResponse(
        @Schema(description = "페이지에 해당하는 회원 리스트")
        List<MemberSingleResponse> memberSingleResponses,

        @Schema(description = "전체 페이지 수")
        Integer totalPage,

        @Schema(description = "전체 뉴스레터 수")
        Long totalElements,

        @Schema(description = "현재 페이지가 첫 페이지인지 여부")
        Boolean isFirst,

        @Schema(description = "현재 페이지가 마지막 페이지인지 여부")
        Boolean isLast
) {

    public static MemberPagingResponse of(
            List<MemberSingleResponse> singleResponses, Integer totalPage,
            Long totalElements, Boolean isFirst, Boolean isLast
    ) {
        return new MemberPagingResponse(singleResponses, totalPage, totalElements, isFirst, isLast);
    }
}
