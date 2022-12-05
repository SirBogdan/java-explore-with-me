package ru.practicum.category;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryDtoShort;

/**
 * Mapper to/from DTO, linked to {@link Category}
 */
public class CategoryMapper {

    public static Category fromCategoryDtoShort(CategoryDtoShort categoryDtoShort) {
        return new Category(categoryDtoShort.getName());
    }

    public static Category fromCategoryDto(CategoryDto categoryDto) {
        return new Category(categoryDto.getId(), categoryDto.getName());
    }

    public static CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }
}
