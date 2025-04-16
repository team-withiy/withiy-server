package com.server.domain.term.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.server.domain.term.dto.TermDto;
import com.server.domain.term.service.TermService;
import com.server.global.dto.ApiResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/term")
public class TermController {
    private final TermService termService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    @Operation(summary = "Withiy 사용 약관 조회", description = "DB에 포함된 모든 약관 리턴")
    public ApiResponseDto<List<TermDto>> getTerms() {
        List<TermDto> termDto = termService.getAllTerms();
        return ApiResponseDto.success(HttpStatus.OK.value(), termDto);
    }

}
