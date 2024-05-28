package com.api.TaveShot.domain.newsletter.domain;

import com.api.TaveShot.domain.base.BaseEntity;
import com.api.TaveShot.domain.newsletter.letter.dto.NewsletterCreateRequest;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "newsletter_event",
            joinColumns = @JoinColumn(name = "newsletter_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private List<Event> events = new ArrayList<>();

    @Builder
    public Newsletter(String title, String content, LetterType letterType, List<Event> events) {
        this.title = title;
        this.content = content;
        this.letterType = letterType;
        this.events = events;
    }

    public static Newsletter createNewsletter(NewsletterCreateRequest request, List<Event> events) {
        return Newsletter.builder()
                .title(request.title())
                .content(request.content())
                .letterType(LetterType.valueOf(request.letterType()))
                .events(events)
                .build();
    }

    public void letterSent() {
        this.sent = true;
    }
}