package com.api.TaveShot.domain.newsletter.client.repository;

import com.api.TaveShot.domain.newsletter.client.domain.EmailToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EmailTokenRepository extends JpaRepository<EmailToken, String> {
    Optional<EmailToken> findByIdAndExpirationDateAfterAndExpired(String emailTokenId, LocalDateTime now, boolean expired);
}
