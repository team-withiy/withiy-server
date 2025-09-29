package com.server.domain.category.service;

import com.server.domain.category.dto.CategoryDto;
import com.server.domain.category.dto.CreateCategoryDto;
import com.server.domain.category.entity.Category;
import com.server.domain.category.repository.CategoryRepository;
import com.server.global.error.code.CategoryErrorCode;
import com.server.global.error.exception.BusinessException;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryRepository categoryRepository;

	@Transactional
	public CategoryDto createCategory(CreateCategoryDto createCategoryDto) {
		Category category = new Category(createCategoryDto.getName(), createCategoryDto.getIcon());
		categoryRepository.save(category);
		return CategoryDto.builder()
			.id(category.getId())
			.name(category.getName())
			.icon(category.getIcon())
			.build();
	}

	public List<CategoryDto> getCategories() {
		List<Category> categories = categoryRepository.findAll();
		List<CategoryDto> categoryDtos = new ArrayList<>();
		for (Category c : categories) {
			categoryDtos.add(CategoryDto.from(c));
		}
		return categoryDtos;
	}

	public Category getCategoryByName(String name) {
		return categoryRepository.findByName(name)
			.orElseThrow(() -> new BusinessException(CategoryErrorCode.NOT_FOUND));
	}

    public Category findById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(CategoryErrorCode.NOT_FOUND));
    }
}
