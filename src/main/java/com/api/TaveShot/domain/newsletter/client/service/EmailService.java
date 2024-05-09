package com.api.TaveShot.domain.newsletter.client.service;

import com.api.TaveShot.domain.Member.domain.Member;
import com.api.TaveShot.domain.Member.repository.MemberRepository;
//import com.api.TaveShot.domain.newsletter.client.domain.EmailToken;
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

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    @Transactional
    public boolean verifyEmail(String token) throws ApiException {

        try {
            String memberId = jwtProvider.getUserIdFromToken(token);

            Member member = memberRepository.findById(Long.parseLong(memberId))
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
