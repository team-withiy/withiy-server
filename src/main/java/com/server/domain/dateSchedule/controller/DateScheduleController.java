package com.server.domain.dateSchedule.controller;

import com.server.domain.dateSchedule.dto.DateSchedCreateRequest;
import com.server.domain.dateSchedule.dto.DateSchedResponse;
import com.server.domain.dateSchedule.dto.DateSchedUpdateRequest;
import com.server.domain.dateSchedule.service.DateSchedFacade;
import com.server.domain.user.entity.User;
import com.server.global.dto.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/date-schedule")
public class DateScheduleController {
    private final DateSchedFacade dateSchedFacade;

    @PostMapping
    @Operation(summary = "일정 등록 API", description = "사용자가 일정을 등록하는 API")
    public ApiResponseDto<Void> createDateSchedule(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody DateSchedCreateRequest request
    ) {
        dateSchedFacade.createDateSchedule(user, request);
        return ApiResponseDto.success(HttpStatus.OK.value(), null);
    }

    @GetMapping
    @Operation(summary = "일정 조회 API", description = "사용자가 등록한 일정을 조회하는 API")
    public ApiResponseDto<List<DateSchedResponse>> getDateSchedule(
            @AuthenticationPrincipal User user,
            @RequestParam
            @Parameter(description = "month, day 만 가능") String format,
            @RequestParam
            @Parameter(description = "month 일때 yyyy-MM 형식, day 일때 yyyy-MM-dd") String date
    ) {
        List<DateSchedResponse> responses = dateSchedFacade.getDateSchedule(user, format, date);
        return ApiResponseDto.success(HttpStatus.OK.value(), responses);
    }

    @PatchMapping("/{dateSchedId}")
    @Operation(summary = "일정 수정 API", description = "사용자가 등록한 일정을 수정하는 API")
    public ApiResponseDto<Void> updatePlaceInDateSchedule(
            @AuthenticationPrincipal User user,
            @PathVariable Long dateSchedId,
            @Valid @RequestBody DateSchedUpdateRequest request
    ) {
        dateSchedFacade.updatePlaceInDateSchedule(user, dateSchedId, request);
        return ApiResponseDto.success(HttpStatus.OK.value(), null);
    }

}
