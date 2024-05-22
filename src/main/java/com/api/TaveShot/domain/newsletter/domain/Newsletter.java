package com.api.TaveShot.domain.newsletter.domain;

import com.api.TaveShot.domain.base.BaseEntity;
import com.api.TaveShot.domain.newsletter.letter.dto.NewsletterCreateRequest;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

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

    @OneToMany(mappedBy = "newsletter", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<NewsletterEvent> newsletterEvents = new HashSet<>();

    @Builder
    public Newsletter(String title, String content, LetterType letterType, List<Event> events) {
        this.title = title;
        this.content = content;
        this.letterType = letterType;
        this.newsletterEvents = new HashSet<>();
        for (Event event : events) {
            this.newsletterEvents.add(new NewsletterEvent(this, event));
        }
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

    public List<Event> getEvents() {
        return this.newsletterEvents.stream().map(NewsletterEvent::getEvent).collect(Collectors.toList());
    }
}
