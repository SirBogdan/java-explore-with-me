package ru.practicum.category.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

/**
 * Shot version of data transfer object for {@link ru.practicum.category.Category}
 *
 * @see ru.practicum.category.CategoryServiceImpl#createCategory(CategoryDtoShort)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CategoryDtoShort {
    @NotBlank
    private String name;
}
