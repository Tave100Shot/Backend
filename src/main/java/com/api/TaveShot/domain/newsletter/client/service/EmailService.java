package com.api.TaveShot.domain.newsletter.client.service;

import com.api.TaveShot.domain.Member.domain.Member;
import com.api.TaveShot.domain.Member.repository.MemberRepository;
import com.api.TaveShot.domain.newsletter.client.domain.EmailToken;
import com.api.TaveShot.global.exception.ApiException;
import com.api.TaveShot.global.exception.ErrorType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailTokenService emailTokenService;
    private final MemberRepository memberRepository;

    @Transactional
    public boolean verifyEmail(String token) throws ApiException {
        EmailToken emailToken = emailTokenService.findByIdAndExpirationDateAfterAndExpired(token);
        emailToken.useToken();

        Member member = memberRepository.findById(emailToken.getMemberId())
                .orElseThrow(() -> new ApiException(ErrorType._USER_NOT_FOUND_DB));

        if (member.isEmailVerified()) {
            throw new ApiException(ErrorType._INVALID_VERIFICATION_LINK);
        }

        member.emailVerifiedSuccess();
        memberRepository.save(member);
        return true;
    }
}

