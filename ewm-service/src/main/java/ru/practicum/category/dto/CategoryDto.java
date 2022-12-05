package ru.practicum.category.dto;

import lombok.*;

/**
 * Data transfer object for {@link ru.practicum.category.Category}
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CategoryDto {
    private long id;
    private String name;
}
