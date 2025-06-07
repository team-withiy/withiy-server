package com.server.domain.category.controller;

import com.server.domain.category.dto.CategoryDto;
import com.server.domain.category.dto.CreateCategoryDto;
import com.server.domain.category.service.CategoryService;

import com.server.global.dto.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    @Operation(summary = "카테고리 가져오기", description = "카테고리 dto 반환")
    public ApiResponseDto<List<CategoryDto>> getCategories() {
        List<CategoryDto> categoryDtos = categoryService.getCategories();
        return ApiResponseDto.success(HttpStatus.OK.value(), categoryDtos);
    }


    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')") // 추후 관리자로 변경
    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    @Operation(summary = "카테고리 추가", description = "카테고리 추가 api")
    public ApiResponseDto<CategoryDto> createCategory(
            @RequestBody CreateCategoryDto createCategoryDto) {
        CategoryDto categoryDto = categoryService.createCategory(createCategoryDto);
        return ApiResponseDto.success(HttpStatus.OK.value(), categoryDto);
    }
}
