package com.api.TaveShot.domain.newsletter.domain;

import com.api.TaveShot.domain.base.BaseEntity;
import com.api.TaveShot.domain.newsletter.admin.dto.EventCreateRequest;
import com.api.TaveShot.domain.newsletter.admin.editor.EventEditor;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Event extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;

    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private LetterType letterType;

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<NewsletterEvent> newsletterEvents = new HashSet<>();

    public static Event from(final EventCreateRequest request) {
        String inputType = request.letterType();
        LetterType letterType = LetterType.findLetterTypeByValue(inputType);

        return Event.builder()
                .title(request.title())
                .content(request.content())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .letterType(letterType)
                .build();
    }

    public void edit(EventEditor eventEditor) {
        title = eventEditor.getTitle();
        content = eventEditor.getContent();
        startDate = eventEditor.getStartDate();
        endDate = eventEditor.getEndDate();
    }

    public EventEditor.EventEditorBuilder initEditor() {
        return EventEditor.builder()
                .title(this.title)
                .content(this.content)
                .startDate(this.startDate)
                .endDate(this.endDate);
    }
}
