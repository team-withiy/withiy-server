package com.server.domain.category.controller;

import com.server.domain.category.dto.CategoryDto;
import com.server.domain.category.dto.CreateCategoryDto;
import com.server.domain.category.service.CategoryService;
import com.server.global.dto.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/categories")
@Tag(name = "Category", description = "카테고리 관련 API")
public class CategoryController {

	private final CategoryService categoryService;

	@ResponseStatus(HttpStatus.OK)
	@GetMapping
	@Operation(summary = "[공용] 카테고리 가져오기", description = "카테고리 dto 반환")
	public ApiResponseDto<List<CategoryDto>> getCategories() {
		return ApiResponseDto.success(HttpStatus.OK.value(), categoryService.getCategories());
	}


	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')") // 추후 관리자로 변경
	@ResponseStatus(HttpStatus.OK)
	@PostMapping
	@Operation(summary = "[관리자] 카테고리 추가", description = "카테고리 추가 api")
	@SecurityRequirement(name = "bearerAuth")
	public ApiResponseDto<CategoryDto> createCategory(
		@RequestBody CreateCategoryDto createCategoryDto) {
		return ApiResponseDto.success(HttpStatus.OK.value(),
			categoryService.createCategory(createCategoryDto));
	}
}
