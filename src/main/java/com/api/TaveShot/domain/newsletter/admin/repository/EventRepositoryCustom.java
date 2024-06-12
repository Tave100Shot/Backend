package com.api.TaveShot.domain.newsletter.admin.repository;

import com.api.TaveShot.domain.newsletter.admin.dto.EventSingleResponse;
import com.api.TaveShot.domain.newsletter.domain.Event;

import com.api.TaveShot.domain.newsletter.domain.Newsletter;
import java.time.LocalDate;
import java.util.List;

public interface EventRepositoryCustom {

    Event findByIdActivated(final Long eventId);

    List<Event> findEventsByDate(LocalDate date);

    List<Event> findEventsForNewsletter(LocalDate endOfWeek, LocalDate now);

    List<EventSingleResponse> getSortedEventSingleResponses(Newsletter newsletter);
}
