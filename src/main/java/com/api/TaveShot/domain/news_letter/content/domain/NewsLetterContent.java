package com.api.TaveShot.domain.news_letter.content.domain;

import com.api.TaveShot.domain.news_letter.main.domain.SubscriptionType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
@Builder
public class NewsLetterContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private SubscriptionType subscriptionType;

    @Column(length = 50000, nullable = false)
    private String content;

    @Column(nullable = false)
    private Integer dayOfMonth;
}
