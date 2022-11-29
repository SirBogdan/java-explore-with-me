package ru.practicum.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.CustomPageRequest;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryDtoShort;
import ru.practicum.exceptions.ObjectNotFoundException;
import ru.practicum.exceptions.ValidationException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryDto createCategory(CategoryDtoShort categoryDtoShort) {
        Category category = CategoryMapper.fromCategoryDtoShort(categoryDtoShort);
        category = categoryRepository.save(category);
        log.info("Создана категория: {}", category);
        return CategoryMapper.toCategoryDto(category);
    }

    @Transactional
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        Category category = CategoryMapper.fromCategoryDto(categoryDto);
        long id = category.getId();
        categoryRepository.findById(id).orElseThrow(
                () -> new ValidationException(String.format(
                        "Ошибка: категории с id %d не существует", id)));

        if (category.getName() == null) {
            throw new ValidationException("Нельзя поменять название категории на null");
        }
        category = categoryRepository.save(category);
        log.info("Обновлена категория: {}", category);
        return CategoryMapper.toCategoryDto(category);
    }

    public List<CategoryDto> getAllCategories(int from, int size) {
        Pageable pageable = CustomPageRequest.of(from, size);

        List<CategoryDto> categoryDtoList = categoryRepository.findAll(pageable)
                .stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
        log.info("Запрошены категории: {}", categoryDtoList);

        return categoryDtoList;
    }

    public CategoryDto getCategoryById(long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new ObjectNotFoundException(String.format(
                        "Ошибка: пользователя с id %d не существует", categoryId)));
        log.info("Запрошена категория: {}", category);

        return CategoryMapper.toCategoryDto(category);
    }

    @Transactional
    public void deleteCategory(long categoryId) {
        categoryRepository.deleteById(categoryId);
    }
}
