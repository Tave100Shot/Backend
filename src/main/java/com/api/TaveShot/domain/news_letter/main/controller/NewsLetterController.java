package com.api.TaveShot.domain.news_letter.main.controller;

import com.api.TaveShot.domain.news_letter.main.dto.NewsLetterRequestDto;
import com.api.TaveShot.domain.news_letter.main.service.NewsLetterService;
import com.api.TaveShot.global.success.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class NewsLetterController {
    private final NewsLetterService newsLetterService;

    @Operation(summary = "뉴스레터 구독", description = "뉴스레터를 구독합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "뉴스레터 구독 성공",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Long.class)))
    })
    @PostMapping("/newsletter")
    public SuccessResponse<Long> subscribe(@RequestBody final NewsLetterRequestDto requestDto){
        Long subscribeId = newsLetterService.subscribe(requestDto);
        return new SuccessResponse<>(subscribeId);
    }
}
