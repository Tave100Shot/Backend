package com.api.TaveShot.domain.newsletter.admin.repository;

import com.api.TaveShot.domain.newsletter.domain.NewsletterEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsletterEventRepository extends JpaRepository<NewsletterEvent, Long> {
}
