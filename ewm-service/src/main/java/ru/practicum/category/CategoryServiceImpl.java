package ru.practicum.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryDtoShort;
import ru.practicum.exceptions.ObjectNotFoundException;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.utils.CustomPageRequest;
import ru.practicum.utils.NPEChecker;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Base implementation of service-layer interface, containing business-logic and linked to {@link Category}
 *
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryDto createCategory(CategoryDtoShort categoryDtoShort) {
        NPEChecker.checkObjNullValue(categoryDtoShort);
        Category category = CategoryMapper.fromCategoryDtoShort(categoryDtoShort);
        category = categoryRepository.save(category);
        log.info("Создана категория: {}", category);
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        NPEChecker.checkObjNullValue(categoryDto);
        Category category = CategoryMapper.fromCategoryDto(categoryDto);

        if (category.getName() == null) {
            throw new ValidationException("Нельзя поменять название категории на null");
        }
        int updatedStringCount = categoryRepository.updateCategory(category.getId(), category.getName());
        if (updatedStringCount != 1) {
            throw new ValidationException(String.format(
                    "Ошибка: категории с id %d не существует", category.getId()));
        }
        log.info("Обновлена категория: {}", category);
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public List<CategoryDto> getAllCategories(int from, int size) {
        Pageable pageable = CustomPageRequest.of(from, size);

        List<CategoryDto> categoryDtoList = categoryRepository.findAll(pageable)
                .stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
        log.info("Запрошены категории: {}", categoryDtoList);

        return categoryDtoList;
    }

    @Override
    public CategoryDto getCategoryById(long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new ObjectNotFoundException(String.format(
                        "Ошибка: пользователя с id %d не существует", categoryId)));
        log.info("Запрошена категория: {}", category);

        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional
    public void deleteCategory(long categoryId) {
        categoryRepository.deleteById(categoryId);
    }
}
