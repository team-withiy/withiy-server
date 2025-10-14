package com.server.domain.category.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.server.domain.category.dto.CategoryDto;
import com.server.domain.category.dto.CreateCategoryDto;
import com.server.domain.category.entity.Category;
import com.server.domain.category.repository.CategoryRepository;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

	@Mock
	private CategoryRepository categoryRepository;

	@InjectMocks
	private CategoryService categoryService;

	@Test
	@DisplayName("카테고리 생성 테스트")
	void createCategory_shouldReturnCategoryDto() {
		// given
		CreateCategoryDto createCategoryDto =
			CreateCategoryDto.builder().name("테스트 카테고리").icon("test_icon")
				.description("test_description").build();

		Category savedCategory = new Category("테스트 카테고리", "test_icon", "테스트 설명");
		// ID 직접 설정
		ReflectionTestUtils.setField(savedCategory, "id", 1L);

		// mock 설정: CategoryRepository.save() 호출 시 savedCategory 반환하도록 설정
		when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
			Category argument = invocation.getArgument(0);
			// 저장 시 ID 설정 (실제 DB 동작 시뮬레이션)
			ReflectionTestUtils.setField(argument, "id", 1L);
			return argument;
		});

		// when
		CategoryDto result = categoryService.createCategory(createCategoryDto);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(1L);
		assertThat(result.getName()).isEqualTo("테스트 카테고리");
		assertThat(result.getIcon()).isEqualTo("test_icon");
		verify(categoryRepository, times(1)).save(any(Category.class));
	}

	@Test
	@DisplayName("모든 카테고리 조회 테스트")
	void getCategories_shouldReturnAllCategories() {
		// given
		Category category1 = new Category("카테고리1", "icon1", "description1");
		ReflectionTestUtils.setField(category1, "id", 1L);

		Category category2 = new Category("카테고리2", "icon2", "description2");
		ReflectionTestUtils.setField(category2, "id", 2L);

		List<Category> categoryList = Arrays.asList(category1, category2);

		when(categoryRepository.findAll()).thenReturn(categoryList);

		// when
		List<CategoryDto> result = categoryService.getCategories();

		// then
		assertThat(result).isNotNull();
		assertThat(result.size()).isEqualTo(2);
		assertThat(result.get(0).getId()).isEqualTo(1L);
		assertThat(result.get(0).getName()).isEqualTo("카테고리1");
		assertThat(result.get(0).getIcon()).isEqualTo("icon1");
		assertThat(result.get(1).getId()).isEqualTo(2L);
		assertThat(result.get(1).getName()).isEqualTo("카테고리2");
		assertThat(result.get(1).getIcon()).isEqualTo("icon2");
		verify(categoryRepository, times(1)).findAll();
	}
}
