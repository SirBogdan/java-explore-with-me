package ru.practicum.compilation;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository, linked to {@link Compilation}
 */
public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    List<Compilation> getAllByPinned(boolean pinned, Pageable pageable);
}
