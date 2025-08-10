package com.server.domain.event.repository;


import com.server.domain.event.entity.Event;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {

	Event findByTitleAndStartDate(String title, LocalDate startDate);

	Optional<List<Event>> findByGenre(String genre);
}