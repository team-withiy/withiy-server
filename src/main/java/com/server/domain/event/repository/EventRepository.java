package com.server.domain.event.repository;


import com.server.domain.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface EventRepository extends JpaRepository<Event, Long> {
    Event findByTitleAndStartDate(String title, LocalDate startDate);
}