package ru.practicum.area;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository, linked to {@link Area}
 */
public interface AreaRepository extends JpaRepository<Area, Long> {

    Area findByLatAndLon(float lan, float lon);
}
