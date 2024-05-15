package com.api.TaveShot.domain.newsletter.client.service;

import static com.api.TaveShot.global.util.SecurityUtil.getCurrentMember;

import com.api.TaveShot.domain.Member.domain.Member;
import com.api.TaveShot.global.exception.ApiException;
import com.api.TaveShot.global.exception.ErrorType;
import com.api.TaveShot.global.security.jwt.JwtProvider;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailTokenService {

    private final EmailSenderService emailService;
    private final JwtProvider jwtProvider;

    // 이메일 인증 토큰 생성
    public String createEmailToken() {
        Member currentMember = getCurrentMember();

        if (currentMember.isEmailVerified()) {
            throw new ApiException(ErrorType._EMAIL_ALREADY_VERIFIED);
        }

        String receiverEmail = currentMember.getGitEmail();

        // JWT 토큰을 생성하고 만료 시간 설정
        String jwtToken = jwtProvider.generateJwtTokenForEmail(String.valueOf(currentMember.getId()));

        String url = "http://43.203.64.45:8080/api/email/verify?token=" + jwtToken;
        //String url = "http://localhost:8081/api/email/verify?token=" + jwtToken;
        String htmlContent = getHtmlContent(url);

        try {
            emailService.sendEmail(receiverEmail, "이메일 인증", htmlContent);
        } catch (MessagingException e) {
            throw new ApiException(ErrorType._EMAIL_SEND_FAILED);
        }

        return jwtToken;
    }

    private String getHtmlContent(final String url) {
        return "<html><body><a href='" + url + "'>여기를 클릭하여 이메일을 인증하세요.</a></body></html>";
    }
}
