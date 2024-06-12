package com.api.TaveShot.domain.newsletter.admin.repository;

import com.api.TaveShot.domain.newsletter.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long>, EventRepositoryCustom {
}
