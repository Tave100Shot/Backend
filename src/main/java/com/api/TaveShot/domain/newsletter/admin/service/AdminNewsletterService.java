package com.api.TaveShot.domain.newsletter.admin.service;

import com.api.TaveShot.domain.newsletter.admin.dto.NewsletterCreateRequest;
import com.api.TaveShot.domain.newsletter.admin.dto.NewsletterPagingResponse;
import com.api.TaveShot.domain.newsletter.admin.dto.NewsletterSingleResponse;
import com.api.TaveShot.domain.newsletter.admin.dto.NewsletterUpdateRequest;
import com.api.TaveShot.domain.newsletter.admin.editor.NewsletterEditor;
import com.api.TaveShot.domain.newsletter.admin.editor.NewsletterEditor.NewsletterEditorBuilder;
import com.api.TaveShot.domain.newsletter.domain.LetterType;
import com.api.TaveShot.domain.newsletter.domain.Newsletter;
import com.api.TaveShot.domain.newsletter.repository.NewsletterRepository;
import com.api.TaveShot.global.exception.ApiException;
import com.api.TaveShot.global.exception.ErrorType;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminNewsletterService {

    private final NewsletterRepository newsletterRepository;

    @Transactional
    public Long register(final NewsletterCreateRequest request) {
        Newsletter createNewsletter = Newsletter.from(request);
        Newsletter newsletter = newsletterRepository.save(createNewsletter);
        return newsletter.getId();
    }

    public NewsletterSingleResponse findById(final Long newsletterId) {
        Newsletter findNewsletter = newsletterRepository.findByIdActivated(newsletterId);
        return NewsletterSingleResponse.from(findNewsletter);
    }

    public NewsletterPagingResponse getPaging(final String inputLetterType, final String containWord, final Pageable pageable) {
        // Type validated
        LetterType letterType = LetterType.findLetterTypeByValue(inputLetterType);

        // Type 개수 여러개 판단
        List<LetterType> letterTypes = getLetterType(letterType);

        Page<Newsletter> newsletterPage = newsletterRepository.getPaging(letterTypes, containWord, pageable);

        List<Newsletter> newsletters = newsletterPage.getContent();
        List<NewsletterSingleResponse> singleResponses = newsletters.stream()
                .map(NewsletterSingleResponse::from)
                .toList();

        NewsletterPagingResponse newsletterPagingResponse = NewsletterPagingResponse.of(singleResponses, newsletterPage.getTotalPages(),
                newsletterPage.getTotalElements(), newsletterPage.isFirst(), newsletterPage.isLast());

        return newsletterPagingResponse;
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

    @Transactional
    public Long delete(final Long newsletterId) {
        Newsletter newsletter = newsletterRepository.findByIdActivated(newsletterId);
        newsletter.deactivate();    // 비활성화
        return newsletter.getId();
    }

    @Transactional
    public Long edit(final NewsletterUpdateRequest request) {
        Long newsletterId = request.newsletterId();
        Newsletter findNewsletter = newsletterRepository.findByIdActivated(newsletterId);

        NewsletterEditorBuilder editorBuilder = findNewsletter.initEditor();
        NewsletterEditor editor = editorBuilder.title(request.title())
                .content(request.content())
                .build();

        findNewsletter.edit(editor);
        return newsletterId;
    }
}
