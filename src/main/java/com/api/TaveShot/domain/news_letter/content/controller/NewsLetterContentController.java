package com.api.TaveShot.domain.news_letter.content.controller;

import com.api.TaveShot.domain.news_letter.content.domain.NewsLetterContent;
import com.api.TaveShot.domain.news_letter.content.dto.NewsLetterContentRequestDto;
import com.api.TaveShot.domain.news_letter.content.service.NewsLetterContentService;
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

@RequestMapping("api/newsletter")
@RequiredArgsConstructor
@RestController
public class NewsLetterContentController {


    private final NewsLetterContentService newsLetterContentService;

    @Operation(summary = "해당 뉴스레터 저장", description = "해당 뉴스레터를 저장합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "뉴스레터 저장 성공",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Long.class)))
    })
    @PostMapping("/content")
    public NewsLetterContent addContent(@RequestBody NewsLetterContentRequestDto contentRequestDto) {
        return newsLetterContentService.saveContent(contentRequestDto);
    }
}

