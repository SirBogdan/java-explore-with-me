package ru.practicum.category;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryDtoShort;

import java.util.List;

@RestController
@Slf4j
public class CategoryController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/admin/categories")
    public CategoryDto createCategory(@RequestBody @Validated CategoryDtoShort categoryDtoShort) {
        CategoryDto categoryDto = categoryService.createCategory(categoryDtoShort);
        log.info("Создана категория: {}", categoryDto);
        return categoryDto;
    }

    @PatchMapping("/admin/categories")
    public CategoryDto updateCategory(@RequestBody @Validated CategoryDto categoryDto) {
        CategoryDto categoryDtoUpdated = categoryService.updateCategory(categoryDto);
        log.info("Обновлена категория: {}", categoryDtoUpdated);
        return categoryDtoUpdated;
    }

    @GetMapping("/categories")
    public List<CategoryDto> getAllCategories(@RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @RequestParam(name = "size", defaultValue = "10") Integer size) {
        List<CategoryDto> categoryDtoList = categoryService.getAllCategories(from, size);
        log.info("Запрошен список категорий: {}", categoryDtoList);
        return categoryDtoList;
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto getCategoryById(@PathVariable("catId") long catId) {
        CategoryDto categoryDto = categoryService.getCategoryById(catId);
        log.info("Запрошена категория: {}", categoryDto);
        return categoryDto;
    }

    @DeleteMapping("/admin/categories/{catId}")
    public void deleteCategory(@PathVariable("catId") long catId) {
        categoryService.deleteCategory(catId);
    }
}
