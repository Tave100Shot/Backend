package com.api.TaveShot.domain.newsletter.client.domain;

import com.api.TaveShot.domain.Member.domain.Member;
import com.api.TaveShot.domain.newsletter.domain.LetterType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private LetterType letterType;

}
