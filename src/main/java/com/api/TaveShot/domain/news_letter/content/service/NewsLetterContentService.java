package com.api.TaveShot.domain.news_letter.content.service;

import com.api.TaveShot.domain.news_letter.content.domain.NewsLetterContent;
import com.api.TaveShot.domain.news_letter.content.repository.NewsLetterContentRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewsLetterContentService {

    private final NewsLetterContentRepository newsLetterContentRepository;

    public NewsLetterContent saveContent(NewsLetterContent content) {
        return newsLetterContentRepository.save(content);
    }
}

