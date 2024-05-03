package com.api.TaveShot.domain.newsletter.client.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailToken {

    private static final long EMAIL_TOKEN_EXPIRATION_TIME_VALUE = 5L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime expirationDate;

    private boolean expired;

    private Long memberId;


    // 이메일 인증 토큰 생성
    public static EmailToken createEmailToken(Long memberId) {
        EmailToken emailToken = new EmailToken();
        emailToken.expirationDate = LocalDateTime.now().plusMinutes(EMAIL_TOKEN_EXPIRATION_TIME_VALUE); // 5분 후 만료
        emailToken.expired = false;
        emailToken.memberId = memberId;

        return emailToken;
    }

    // 토큰 만료
    public void useToken() {
        this.expired = true;
    }

}
