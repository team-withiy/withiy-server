package com.server.domain.event.controller;

import com.server.domain.category.dto.CategoryDto;
import com.server.domain.event.dto.EventDto;
import com.server.domain.event.service.EventService;
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
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;


    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')") //추후 관리자로 변경
    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    @Operation(summary = "이벤트 저장하기", description = "인터파크 티켓 랭킹 탑 50 기반 1주마다 이벤트 저장")
    public ApiResponseDto<List<EventDto>> saveEvents() throws Exception {
        List<EventDto> eventDtos = eventService.saveEvents();
        return ApiResponseDto.success(HttpStatus.OK.value(), eventDtos);
    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    @Operation(summary = "이벤트 정보 가져오기", description = "인터파크 티켓 랭킹 탑 50 기반 각 장르별 이벤트 dto 반환")
    public ApiResponseDto<List<EventDto>> getEvents() {
        List<EventDto> eventDtos = eventService.getEvents();
        return ApiResponseDto.success(HttpStatus.OK.value(), eventDtos);
    }
}
