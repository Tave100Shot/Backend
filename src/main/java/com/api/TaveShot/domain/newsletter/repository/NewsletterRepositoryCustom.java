package com.api.TaveShot.domain.newsletter.repository;

import com.api.TaveShot.domain.newsletter.domain.LetterType;
import com.api.TaveShot.domain.newsletter.domain.Newsletter;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NewsletterRepositoryCustom {

    Newsletter findByIdActivated(final Long newsletterId);

    Page<Newsletter> getPaging(List<LetterType> letterTypes, String containWord,
                               Pageable pageable);

    List<Newsletter> findRecent6(Long LIMIT_VALUE);
}
