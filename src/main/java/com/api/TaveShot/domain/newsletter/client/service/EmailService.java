package com.api.TaveShot.domain.newsletter.client.service;

import com.api.TaveShot.domain.Member.domain.Member;
import com.api.TaveShot.domain.Member.service.MemberService;
import com.api.TaveShot.global.exception.ApiException;
import com.api.TaveShot.global.exception.ErrorType;
import com.api.TaveShot.global.security.jwt.JwtProvider;
import io.jsonwebtoken.JwtException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final MemberService memberService;
    private final JwtProvider jwtProvider;

    @Transactional
    public Long verifyEmail(String token) throws ApiException {
        try {
            String memberId = jwtProvider.getUserIdFromToken(token);
            Member member = memberService.findById(Long.parseLong(memberId));
            if (!member.isEmailVerified()) {
                member.emailVerifiedSuccess();
            }

            return member.getId();
        } catch (JwtException | IllegalArgumentException e) {
            throw new ApiException(ErrorType._JWT_PARSING_ERROR);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApiException(ErrorType._STATIC_ERROR_RUNTIME_EXCEPTION);
        }
    }
}
