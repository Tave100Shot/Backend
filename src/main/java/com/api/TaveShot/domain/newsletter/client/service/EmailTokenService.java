package com.api.TaveShot.domain.newsletter.client.service;

import com.api.TaveShot.domain.Member.domain.Member;
import com.api.TaveShot.domain.newsletter.client.domain.EmailToken;
import com.api.TaveShot.domain.newsletter.client.repository.EmailTokenRepository;
import com.api.TaveShot.global.exception.ApiException;
import com.api.TaveShot.global.exception.ErrorType;
import com.api.TaveShot.global.util.SecurityUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailTokenService {

    private final EmailSenderService emailService;
    private final EmailTokenRepository emailTokenRepository;

    // 이메일 인증 토큰 생성
    public Long createEmailToken() {
        Member currentMember = SecurityUtil.getCurrentMember();
        if(currentMember == null)
            throw new ApiException(ErrorType._USER_NOT_FOUND_DB);

        String receiverEmail = currentMember.getGitEmail();

        // 이메일 토큰 저장
        EmailToken emailToken = EmailToken.createEmailToken(currentMember.getId());
        emailTokenRepository.save(emailToken);

        String url = "http://localhost:8081/email/verify?token=" + emailToken.getId();
        String htmlContent = "<html><body><a href='" + url + "'>여기를 클릭하여 이메일을 인증하세요.</a></body></html>";

        try {
            emailService.sendEmail(receiverEmail, "이메일 인증", htmlContent);
        } catch (MessagingException e) {
            log.error("Failed to send email", e);
            throw new ApiException(ErrorType._EMAIL_SEND_FAILED);
        }

        return emailToken.getId();
    }

    // 유효한 토큰 가져오기
    public EmailToken findByIdAndExpirationDateAfterAndExpired(String emailTokenId) throws ApiException {
        return emailTokenRepository.findByIdAndExpirationDateAfterAndExpired(emailTokenId, LocalDateTime.now(), false)
                .orElseThrow(() -> new ApiException(ErrorType._INVALID_EMAIL_TOKEN));
    }

}
