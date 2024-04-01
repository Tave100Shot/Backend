package com.api.TaveShot.domain.news_letter.main.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
@Builder
public class NewsLetter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String nickname;

    @Column(length = 500, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    private SubscriptionType subscriptionType;
}
