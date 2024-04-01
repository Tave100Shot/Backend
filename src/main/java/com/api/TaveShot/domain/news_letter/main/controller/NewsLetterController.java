package com.api.TaveShot.domain.news_letter.main.controller;

import com.api.TaveShot.domain.news_letter.main.dto.NewsLetterRequestDto;
import com.api.TaveShot.domain.news_letter.main.service.NewsLetterService;
import com.api.TaveShot.global.success.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class NewsLetterController {
    private final NewsLetterService newsLetterService;

    @PostMapping("/newsletter")
    public SuccessResponse<Long> subscribe(@RequestBody final NewsLetterRequestDto requestDto){
        Long subscribeId = newsLetterService.subscribe(requestDto);
        return new SuccessResponse<>(subscribeId);
    }
}
