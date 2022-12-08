package ru.practicum.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

/**
 * Repository, linked to {@link Event}
 */
public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    List<Event> findAllByInitiator_Id(long initiatorId, Pageable pageable);

    @Query("select e.id " +
            "from Event e " +
            "where distance(?1, ?2, e.lat, e.lon) <= ?3")
    List<Long> findEventsIdsInArea(float lat, float lon, float radius);
}
