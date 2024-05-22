package com.api.TaveShot.domain.newsletter.letter.repository;

import com.api.TaveShot.domain.newsletter.domain.LetterType;
import com.api.TaveShot.domain.newsletter.domain.Newsletter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NewsletterRepositoryCustom {
    Newsletter findByIdActivated(final Long newsletterId);

    Page<Newsletter> getPaging(List<LetterType> letterTypes, String containWord,
                               Pageable pageable);

    List<Newsletter> findRecent6(Long LIMIT_VALUE);

    List<Newsletter> findByYearAndMonth(int year, int month);
}
