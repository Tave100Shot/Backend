package com.api.TaveShot.domain.base;

import com.api.TaveShot.domain.Member.domain.Member;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@EnableJpaAuditing
@Configuration
public class BaseEntityConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                return Optional.of("Anonymous");
            }

            Object principal = authentication.getPrincipal();
            if (principal instanceof Member) {
                Member member = (Member) principal;
                return Optional.ofNullable(member.getGitLoginId());
            } else {
                return Optional.of("Unknown");
            }
        };
    }
}
