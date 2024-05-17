package com.api.TaveShot.domain.newsletter.domain;

import com.api.TaveShot.domain.base.BaseEntity;
import com.api.TaveShot.domain.newsletter.admin.dto.NewsletterCreateRequest;
import com.api.TaveShot.domain.newsletter.admin.editor.NewsletterEditor;
import com.api.TaveShot.domain.newsletter.admin.editor.NewsletterEditor.NewsletterEditorBuilder;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Newsletter extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;

    @Enumerated(EnumType.STRING)
    private LetterType letterType;
    private boolean sent;

    public static Newsletter from(final NewsletterCreateRequest request) {
        String inputType = request.letterType();
        LetterType letterType = LetterType.findLetterTypeByValue(inputType);

        return Newsletter.builder()
                .title(request.title())
                .content(request.content())
                .letterType(letterType)
                .build();
    }

    public NewsletterEditorBuilder initEditor() {
        return NewsletterEditor.builder()
                .title(title)
                .content(content);
    }

    public void edit(NewsletterEditor newsletterEditor) {
        title = newsletterEditor.getTitle();
        content = newsletterEditor.getContent();
    }

    public void letterSent() {
        sent = true;
    }
}
