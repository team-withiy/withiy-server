package com.server.domain.section.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.server.domain.section.dto.CreateSectionDto;
import com.server.domain.section.dto.HomeSectionDto;
import com.server.domain.section.dto.SectionDto;
import com.server.domain.section.service.SectionService;
import com.server.global.dto.ApiResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/sections")
public class SectionController {
    private final SectionService sectionService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/home")
    @Operation(summary = "홈 화면 섹션 얻기", description = "홈 화면에 존재하는 섹션 정보 반환")
    public ApiResponseDto<List<HomeSectionDto>> getHomeSections() {
        List<HomeSectionDto> sectionDtos = sectionService.getHomeSections();
        return ApiResponseDto.success(HttpStatus.OK.value(), sectionDtos);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')") // 추후 관리자로 변경
    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    @Operation(summary = "섹션 추가", description = "섹션 추가 api")
    public ApiResponseDto<SectionDto> createSection(@RequestBody CreateSectionDto createSectionDto) {
        SectionDto sectionDto = sectionService.createSection(createSectionDto);
        return ApiResponseDto.success(HttpStatus.OK.value(), sectionDto);
    }

}
