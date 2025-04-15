package com.server.domain.category.service;

import com.server.domain.category.dto.CategoryDto;
import com.server.domain.category.dto.CreateCategoryDto;
import com.server.domain.category.entity.Category;
import com.server.domain.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;


    public CategoryDto createCategory(CreateCategoryDto createCategoryDto) {
        Category category = new Category(createCategoryDto.getName());
        categoryRepository.save(category);
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
