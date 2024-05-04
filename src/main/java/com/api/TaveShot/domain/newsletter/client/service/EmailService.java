package com.api.TaveShot.domain.newsletter.client.service;

import com.api.TaveShot.domain.Member.domain.Member;
import com.api.TaveShot.domain.Member.repository.MemberRepository;
import com.api.TaveShot.domain.newsletter.client.domain.EmailToken;
import com.api.TaveShot.global.exception.ApiException;
import com.api.TaveShot.global.exception.ErrorType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailTokenService emailTokenService;
    private final MemberRepository memberRepository;

    @Value("${jwt.secret.key}")
    private String SECRET_KEY;

    @Transactional
    public boolean verifyEmail(String token) throws ApiException {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY.getBytes())
                    .parseClaimsJws(token)
                    .getBody();

            Long tokenId = Long.parseLong(claims.getSubject());
            EmailToken emailToken = emailTokenService.findByIdAndExpirationDateAfterAndExpired(String.valueOf(tokenId));

            if (emailToken.isExpired() || LocalDateTime.now().isAfter(emailToken.getExpirationDate())) {
                throw new ApiException(ErrorType._EXPIRED_EMAIL_TOKEN);
            }

            emailToken.useToken(); // 토큰 사용 처리

            Member member = memberRepository.findById(emailToken.getMemberId())
                    .orElseThrow(() -> new ApiException(ErrorType._USER_NOT_FOUND_DB));

            if (!member.isEmailVerified()) {
                member.emailVerifiedSuccess();
                memberRepository.save(member);
                return true;
            } else {
                throw new ApiException(ErrorType._INVALID_VERIFICATION_LINK);
            }
        } catch (JwtException | IllegalArgumentException e) {
            throw new ApiException(ErrorType._JWT_PARSING_ERROR);
        }
    }
}
