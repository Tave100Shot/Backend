package com.api.TaveShot.domain.newsletter.admin.repository;

import com.api.TaveShot.domain.newsletter.admin.dto.EventSingleResponse;
import com.api.TaveShot.domain.newsletter.domain.Event;
import com.api.TaveShot.domain.newsletter.domain.Newsletter;
import com.api.TaveShot.domain.newsletter.domain.QEvent;
import com.api.TaveShot.domain.newsletter.domain.QNewsletterEvent;
import com.api.TaveShot.global.exception.ApiException;
import com.api.TaveShot.global.exception.ErrorType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import static com.api.TaveShot.domain.newsletter.domain.QEvent.event;

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

    @Override
    public List<Event> findEventsForNewsletter(LocalDate startOfWeek, LocalDate endOfWeek) {
        return jpaQueryFactory
                .selectFrom(event)
                .where(
                        event.startDate.loe(endOfWeek.plusDays(6)), // 시작 날짜가 보내는 날짜보다 일주일 이후인 행사들까지
                        event.endDate.goe(endOfWeek), // 아직 끝나지 않은 행사
                        getActivated()
                )
                .orderBy(event.startDate.asc(), event.endDate.asc())
                .fetch();
    }

    private BooleanExpression getActivated() {
        return event.deleted.isFalse();
    }

    @Override
    public List<EventSingleResponse> getSortedEventSingleResponses(Newsletter newsletter) {
        QEvent qEvent = QEvent.event;
        QNewsletterEvent qNewsletterEvent = QNewsletterEvent.newsletterEvent;

        List<Event> sortedEvents = jpaQueryFactory
                .selectFrom(qEvent)
                .leftJoin(qEvent.newsletterEvents, qNewsletterEvent).fetchJoin()
                .where(qNewsletterEvent.newsletter.eq(newsletter))
                .orderBy(qEvent.startDate.asc(), qEvent.endDate.asc())
                .fetch();

        return sortedEvents.stream()
                .map(EventSingleResponse::from)
                .toList();
    }
}
