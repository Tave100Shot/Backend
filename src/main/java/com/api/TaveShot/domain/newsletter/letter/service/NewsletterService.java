package com.api.TaveShot.domain.newsletter.letter.service;

import com.api.TaveShot.domain.newsletter.domain.LetterType;
import com.api.TaveShot.domain.newsletter.domain.Newsletter;
import com.api.TaveShot.domain.newsletter.letter.dto.*;
import com.api.TaveShot.domain.newsletter.letter.repository.NewsletterRepository;
import com.api.TaveShot.global.exception.ApiException;
import com.api.TaveShot.global.exception.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NewsletterService {

    private final NewsletterRepository newsletterRepository;
    private static final Long LIMIT_VALUE = 6L;

    @Transactional
    public Long register(final NewsletterCreateRequest request) {
        Newsletter newsletter = Newsletter.builder()
                .title(request.title())
                .content(request.content())
                .letterType(LetterType.findLetterTypeByValue(request.letterType()))
                .build();
        Newsletter savedNewsletter = newsletterRepository.save(newsletter);
        return savedNewsletter.getId();
    }

    public NewsletterSingleResponse findById(final Long newsletterId) {
        Newsletter newsletter = newsletterRepository.findById(newsletterId)
                .orElseThrow(() -> new ApiException(ErrorType.NEWSLETTER_NOT_FOUND));
        return NewsletterSingleResponse.from(newsletter);
    }

    public NewsletterPagingResponse getPaging(final String inputLetterType, final String containWord, final Pageable pageable) {
        LetterType letterType = LetterType.findLetterTypeByValue(inputLetterType);
        List<LetterType> letterTypes = getLetterType(letterType);

        Page<Newsletter> newsletterPage = newsletterRepository.getPaging(letterTypes, containWord, pageable);

        List<NewsletterSingleResponse> singleResponses = newsletterPage.getContent().stream()
                .map(NewsletterSingleResponse::from)
                .collect(Collectors.toList());

        return NewsletterPagingResponse.of(singleResponses, newsletterPage.getTotalPages(),
                newsletterPage.getTotalElements(), newsletterPage.isFirst(), newsletterPage.isLast());
    }

    private List<NewsletterSingleResponse> entitiesToDtos(final List<Newsletter> newsletters) {
        return newsletters.stream()
                .map(NewsletterSingleResponse::from)
                .toList();
    }

    public NewsletterRecentResponse getRecent() {
        List<Newsletter> newsletters = newsletterRepository.findRecent6(LIMIT_VALUE);
        List<NewsletterSingleResponse> singleResponses = entitiesToDtos(newsletters);

        return NewsletterRecentResponse.from(singleResponses);
    }

    public NewsletterDateResponse getNewsletterByDate(final Integer year, final Integer month) {
        List<Newsletter> newsletters = newsletterRepository.findByYearAndMonth(year, month);

        List<NewsletterSingleResponse> singleResponses = entitiesToDtos(newsletters);
        NewsletterDateResponse response = NewsletterDateResponse.of(singleResponses, year, month);
        return response;
    }

    private List<LetterType> getLetterType(final LetterType letterType) {
        if (letterType.equals(LetterType.ALL)) {
            List<LetterType> letter = new ArrayList<>();
            letter.add(LetterType.DEV_LETTER);
            letter.add(LetterType.EMPLOYEE_LETTER);
            return letter;
        }

        if (letterType.equals(LetterType.DEV_LETTER)) {
            List<LetterType> letter = new ArrayList<>();
            letter.add(letterType);
            return letter;
        }

        if (letterType.equals(LetterType.EMPLOYEE_LETTER)) {
            List<LetterType> letter = new ArrayList<>();
            letter.add(letterType);
            return letter;
        }

        throw new ApiException(ErrorType.LETTER_TYPE_NOT_FOUND);
    }
}
