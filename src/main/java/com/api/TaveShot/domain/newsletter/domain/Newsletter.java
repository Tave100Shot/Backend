package com.api.TaveShot.domain.newsletter.domain;

import com.api.TaveShot.domain.base.BaseEntity;
import com.api.TaveShot.domain.newsletter.letter.dto.NewsletterCreateRequest;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Newsletter extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Enumerated(EnumType.STRING)
    private LetterType letterType;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    private boolean sent;

    @Builder
    public Newsletter(String title, String content, LetterType letterType) {
        this.title = title;
        this.content = content;
        this.letterType = letterType;
    }

    public static Newsletter createNewsletter(NewsletterCreateRequest request) {
        return Newsletter.builder()
                .title(request.title())
                .content(request.content())
                .letterType(LetterType.valueOf(request.letterType()))
                .build();
    }

    public void letterSent() {
        this.sent = true;
    }
}
