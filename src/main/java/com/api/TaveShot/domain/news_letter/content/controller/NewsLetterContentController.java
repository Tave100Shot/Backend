package com.api.TaveShot.domain.news_letter.content.controller;

import com.api.TaveShot.domain.news_letter.content.domain.NewsLetterContent;
import com.api.TaveShot.domain.news_letter.content.dto.NewsLetterContentRequestDto;
import com.api.TaveShot.domain.news_letter.content.service.NewsLetterContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("api/newsletter")
@RequiredArgsConstructor
@RestController
public class NewsLetterContentController {

    private final NewsLetterContentService newsLetterContentService;

    @PostMapping("/content")
    public NewsLetterContent addContent(@RequestBody NewsLetterContentRequestDto contentRequestDto) {
        return newsLetterContentService.saveContent(contentRequestDto);
    }
}

