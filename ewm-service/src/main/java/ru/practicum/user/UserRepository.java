package ru.practicum.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository, linked to {@link User}
 */
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findUsersByIdInOrderById(List<Long> ids, Pageable pageable);
}
