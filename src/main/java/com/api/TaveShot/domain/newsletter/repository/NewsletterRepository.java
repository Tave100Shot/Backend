package com.api.TaveShot.domain.newsletter.repository;

import com.api.TaveShot.domain.newsletter.domain.LetterType;
import com.api.TaveShot.domain.newsletter.domain.Newsletter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NewsletterRepository extends JpaRepository<Newsletter, Long>, NewsletterRepositoryCustom {
    Optional<Newsletter> findTop1ByLetterTypeOrderByCreatedDateAsc(LetterType letterType);
}

