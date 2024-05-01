package com.api.TaveShot.domain.newsletter.admin.editor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

@Getter
@RequiredArgsConstructor
public class NewsletterEditor {

    private final String title;
    private final String content;

    public static NewsletterEditorBuilder builder() {
        return new NewsletterEditorBuilder();
    }

    public static class NewsletterEditorBuilder {

        private String title;
        private String content;

        NewsletterEditorBuilder() {}

        public NewsletterEditorBuilder title(final String title) {
            if (StringUtils.hasText(title)) {
                this.title = title;
            }
            return this;
        }

        public NewsletterEditorBuilder content(final String content) {
            if (StringUtils.hasText(content)) {
                this.content = content;
            }
            return this;
        }

        public NewsletterEditor build() {
            return new NewsletterEditor(title, content);
        }
    }
}
