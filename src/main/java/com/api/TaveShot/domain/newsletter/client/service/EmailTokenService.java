package com.api.TaveShot.domain.newsletter.client.service;

import static com.api.TaveShot.global.util.SecurityUtil.getCurrentMember;

import com.api.TaveShot.domain.Member.domain.Member;
import com.api.TaveShot.global.exception.ApiException;
import com.api.TaveShot.global.exception.ErrorType;
import com.api.TaveShot.global.security.jwt.JwtProvider;
import jakarta.mail.MessagingException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
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

        if (receiverEmail == null || receiverEmail.isEmpty()) {
            throw new ApiException(ErrorType._EMAIL_NOT_FOUND);
        }

        // JWT 토큰을 생성하고 만료 시간 설정
        String jwtToken = jwtProvider.generateJwtTokenForEmail(String.valueOf(currentMember.getId()));

        String url = "http://43.203.64.45:8080/api/email/verify?token=" + jwtToken;
        String htmlContent = getHtmlContent(url);

        try {
            emailService.sendEmail(receiverEmail, "이메일 인증", htmlContent);
        } catch (MessagingException e) {
            throw new ApiException(ErrorType._EMAIL_SEND_FAILED);
        }

        return jwtToken;
    }

    private String getHtmlContent(final String url) {
        String htmlTemplate;
        try {
            htmlTemplate = loadHtmlTemplate("templates/emailVerification.html");
        } catch (IOException e) {
            throw new ApiException(ErrorType._TEMPLATE_READ_FAILED);
        }

        return htmlTemplate.replace("${url}", url);
    }

    public String loadHtmlTemplate(String fileName) throws IOException {
        ClassPathResource resource = new ClassPathResource(fileName);
        try (InputStream inputStream = resource.getInputStream()) {
            byte[] bytes = inputStream.readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }

}
