package com.api.TaveShot.domain.newsletter.repository;

import com.api.TaveShot.domain.newsletter.domain.EmailSendStatus;
import org.springframework.data.jpa.repository.JpaRepository;


public interface EmailSendStatusRepository extends JpaRepository<EmailSendStatus, Long> {

}
