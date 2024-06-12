package com.api.TaveShot.domain.newsletter.admin.editor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class EventEditor {

    private final String title;
    private final String content;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final boolean hidePeriod;

    public static EventEditorBuilder builder() {
        return new EventEditorBuilder();
    }

    public static class EventEditorBuilder {

        private String title;
        private String content;
        private LocalDate startDate;
        private LocalDate endDate;
        private boolean hidePeriod;

        EventEditorBuilder() {
        }

        public EventEditorBuilder title(final String title) {
            if (StringUtils.hasText(title)) {
                this.title = title;
            }
            return this;
        }

        public EventEditorBuilder content(final String content) {
            if (StringUtils.hasText(content)) {
                this.content = content;
            }
            return this;
        }

        public EventEditorBuilder startDate(final LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public EventEditorBuilder endDate(final LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public EventEditorBuilder hidePeriod(final boolean hidePeriod) {
            this.hidePeriod = hidePeriod;
            return this;
        }

        public EventEditor build() {
            return new EventEditor(title, content, startDate, endDate, hidePeriod);
        }
    }
}
