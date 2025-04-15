package com.server.domain.section.controller;

import com.server.domain.section.dto.CreateSectionDto;
import com.server.domain.section.dto.SectionDto;
import com.server.domain.section.service.SectionService;
import com.server.domain.user.dto.GetUserOutDto;
import com.server.domain.user.entity.User;
import com.server.global.dto.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/section")
public class SectionController {
    private final SectionService sectionService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/home")
    @Operation(summary = "홈 화면 섹션 얻기", description = "홈 화면에 존재하는 섹션 정보 반환")
    public ApiResponseDto<List<SectionDto>> getHomeSections() {
        List<SectionDto> sectionDtos = sectionService.getHomeSections();
        return ApiResponseDto.success(HttpStatus.OK.value(), sectionDtos);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')") //추후 관리자로 변경
    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    @Operation(summary = "섹션 추가", description = "섹션 추가 api")
    public ApiResponseDto<SectionDto> createSection(@RequestBody CreateSectionDto createSectionDto) {
        SectionDto sectionDto = sectionService.createSection(createSectionDto);
        return ApiResponseDto.success(HttpStatus.OK.value(), sectionDto);
    }


}
