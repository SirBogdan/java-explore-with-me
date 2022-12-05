package ru.practicum.event.location;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {

    Location findByLatAndLon(float lan, float lon);
}
