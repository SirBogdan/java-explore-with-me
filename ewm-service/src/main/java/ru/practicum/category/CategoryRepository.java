package ru.practicum.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository, linked to {@link Category}
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Modifying
    @Query("update Category set name = ?2 " +
            "where id = ?1")
    int updateCategory(long id, String name);
}
