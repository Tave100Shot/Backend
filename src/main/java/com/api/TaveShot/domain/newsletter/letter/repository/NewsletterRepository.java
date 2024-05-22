package com.api.TaveShot.domain.newsletter.letter.repository;

import com.api.TaveShot.domain.newsletter.domain.Newsletter;

import org.springframework.data.jpa.repository.JpaRepository;


public interface NewsletterRepository extends JpaRepository<Newsletter, Long>, NewsletterRepositoryCustom {

}
