package ru.practicum.category;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryDtoShort;

import java.util.List;

/**
 * Interface of service-layer, containing business-logic and linked to {@link Category}
 */
public interface CategoryService {
    CategoryDto createCategory(CategoryDtoShort categoryDtoShort);

    CategoryDto updateCategory(CategoryDto categoryDto);

    List<CategoryDto> getAllCategories(int from, int size);

    CategoryDto getCategoryById(long categoryId);

    void deleteCategory(long categoryId);
}
