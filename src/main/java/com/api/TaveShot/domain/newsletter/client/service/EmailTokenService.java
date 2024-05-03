package com.api.TaveShot.domain.newsletter.client.service;

import com.api.TaveShot.domain.newsletter.client.domain.EmailToken;
import com.api.TaveShot.domain.newsletter.client.repository.EmailTokenRepository;
import com.api.TaveShot.global.exception.ApiException;
import com.api.TaveShot.global.exception.ErrorType;
import com.mysema.commons.lang.Assert;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailTokenService {

    private final EmailSenderService emailService;
    private final EmailTokenRepository emailTokenRepository;

    // 이메일 인증 토큰 생성
    public Long createEmailToken(Long memberId, String receiverEmail) {

        Assert.notNull(memberId, "memberId는 필수입니다");
        Assert.hasText(receiverEmail, "receiverEmail은 필수입니다.");

        // 이메일 토큰 저장
        EmailToken emailToken = EmailToken.createEmailToken(memberId);
        emailTokenRepository.save(emailToken);

        // 이메일 전송
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(receiverEmail);
        mailMessage.setSubject("이메일 인증");
        //mailMessage.setText("https://100shot.net/confirm-email?token="+emailToken.getId()); //배포 버전
        mailMessage.setText("http://localhost:3000/confirm-email?token="+emailToken.getId()); //로컬 주소
        emailService.sendEmail(mailMessage);


        try {
            emailService.sendEmail(mailMessage);
        } catch (MailException e) {
            throw new ApiException(ErrorType.EMAIL_SEND_FAILED);
        }

        return emailToken.getId();
    }



    // 유효한 토큰 가져오기
    public EmailToken findByIdAndExpirationDateAfterAndExpired(String emailTokenId) throws ApiException {
        return emailTokenRepository.findByIdAndExpirationDateAfterAndExpired(emailTokenId, LocalDateTime.now(), false)
                .orElseThrow(() -> new ApiException(ErrorType._INVALID_EMAIL_TOKEN));
    }

}
