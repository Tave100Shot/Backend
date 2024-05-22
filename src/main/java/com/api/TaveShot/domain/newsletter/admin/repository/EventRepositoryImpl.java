package com.api.TaveShot.domain.newsletter.admin.repository;

import com.api.TaveShot.domain.newsletter.domain.Event;
import com.api.TaveShot.domain.newsletter.domain.LetterType;
import com.api.TaveShot.global.exception.ApiException;
import com.api.TaveShot.global.exception.ErrorType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;


import java.time.LocalDate;
import java.util.List;

import static com.api.TaveShot.domain.newsletter.domain.QEvent.event;
import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
public class EventRepositoryImpl implements EventRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Event findByIdActivated(final Long eventId) {
        Event findEvent = jpaQueryFactory
                .selectFrom(event)
                .where(
                        event.id.eq(eventId),
                        getActivated()
                )
                .fetchOne();

        if (findEvent == null) {
            throw new ApiException(ErrorType._EVENT_NOT_FOUND);
        }
        return findEvent;
    }

    @Override
    public List<Event> findEventsByDate(LocalDate date) {
        return jpaQueryFactory
                .selectFrom(event)
                .where(
                        event.startDate.loe(date),
                        event.endDate.goe(date),
                        getActivated()
                )
                .fetch();
    }

    private BooleanExpression getActivated() {
        return event.deleted.isFalse();
    }


    private BooleanExpression containsInContentOrTitle(String containWord) {
        if (hasText(containWord)) {
            return event.content.contains(containWord)
                    .or(event.title.contains(containWord));
        }
        return null;
    }

    private Long countQuery(final List<LetterType> letterTypes, final String containWord) {
        return jpaQueryFactory
                .select(Wildcard.count)
                .from(event)
                .where(
                        containsInContentOrTitle(containWord),
                        event.letterType.in(letterTypes),
                        getActivated()
                )
                .fetchOne();
    }
}
