package com.api.TaveShot.domain.newsletter.admin.controller;

import com.api.TaveShot.domain.newsletter.admin.dto.EventCreateRequest;
import com.api.TaveShot.domain.newsletter.admin.dto.EventSingleResponse;
import com.api.TaveShot.domain.newsletter.admin.dto.EventUpdateRequest;
import com.api.TaveShot.domain.newsletter.admin.service.AdminEventService;
import com.api.TaveShot.global.success.SuccessResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Event Admin API", description = "행사 관리자 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/event")
public class AdminEventController {

    private final AdminEventService adminEventService;

    @PostMapping
    public SuccessResponse<Long> registerEvent(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "행사 생성 요청 데이터", required = true
            )
            @Valid @RequestBody EventCreateRequest request
    ) {
        Long eventId = adminEventService.register(request);
        return new SuccessResponse<>(eventId);
    }

    @GetMapping("/{eventId}")
    public SuccessResponse<EventSingleResponse> getSingleEvent(
            @Parameter(description = "조회할 행사의 ID", required = true, example = "1")
            @PathVariable final Long eventId) {
        return new SuccessResponse<>(adminEventService.findById(eventId));
    }

    @GetMapping("/byDate")
    public SuccessResponse<List<EventSingleResponse>> getEventsByDate(
            @Parameter(description = "조회할 날짜", required = true, example = "2024-01-01")
            @RequestParam final LocalDate date) {
        return new SuccessResponse<>(adminEventService.findEventsByDate(date));
    }

    @DeleteMapping("/{eventId}")
    public SuccessResponse<Long> delete(@Parameter(description = "삭제할 행사의 ID", required = true, example = "1")
                                        @PathVariable final Long eventId) {
        return new SuccessResponse<>(adminEventService.delete(eventId));
    }

    @PatchMapping
    public SuccessResponse<Long> edit(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "행사 수정 요청 데이터", required = true) @RequestBody EventUpdateRequest request) {
        return new SuccessResponse<>(adminEventService.edit(request));
    }
}
