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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime expirationDate;

    private boolean expired;

    private Long memberId;

    @Column(nullable = false)
    private String token;

    // 토큰 만료
    public void useToken() {
        this.expired = true;
    }

}
