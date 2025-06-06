package com.server.domain.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.domain.category.dto.CategoryDto;
import com.server.domain.event.dto.CrawlingEventDto;
import com.server.domain.event.dto.CrawlingEventDtoList;
import com.server.domain.event.dto.EventDto;
import com.server.domain.event.service.EventService;
import com.server.global.dto.ApiResponseDto;
import com.server.global.etc.HmacUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;
    private final ObjectMapper objectMapper;

    @Value("${hmac.secret-key}")
    private String SECRET_KEY;



    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/external")
    @Operation(summary = "외부 크롤러에서 이벤트 저장", description = "크롤러가 직접 이벤트 리스트를 전달")
    public ApiResponseDto<List<EventDto>> saveEventsFromExternal(
            HttpServletRequest request,
            @RequestBody CrawlingEventDtoList crawlingEventDtoList) throws Exception {

        // 1. 본문을 JSON 문자열로 다시 직렬화
        String requestBody = objectMapper.writeValueAsString(crawlingEventDtoList);

        // 2. 헤더에서 시그니처 추출
        String signature = request.getHeader("X-Signature");

        // 3. HMAC 계산
        String computedSignature = HmacUtil.hmacSha256(requestBody, SECRET_KEY);

        // 4. 검증 실패시 403 반환
        if (!computedSignature.equals(signature)) {
            log.warn("HMAC 검증 실패: {}", request.getRequestURI());
            throw new Exception("Invalid signature");
        }

        log.info("외부 크롤러에서 호출, HMAC 검증 성공");
        List<EventDto> saved = eventService.saveEvents(crawlingEventDtoList);
        return ApiResponseDto.success(HttpStatus.OK.value(), saved);
    }




    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    @Operation(summary = "이벤트 정보 가져오기", description = "인터파크 티켓 랭킹 탑 50 기반 각 장르별 이벤트 dto 반환")
    public ApiResponseDto<List<EventDto>> getEvents() {
        List<EventDto> eventDtos = eventService.getEvents();
        return ApiResponseDto.success(HttpStatus.OK.value(), eventDtos);
    }



    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{genre}")
    @Operation(summary = "장르별 이벤트 정보 가져오기", description = "각 장르별(musical, concert, sports, exhibit, drama) 이벤트 dto 반환")
    public ApiResponseDto<List<EventDto>> getEventsByGenre(@PathVariable String genre) {
        // 유효한 장르인지 검사
        if (!Arrays.asList("musical", "concert", "sports", "exhibit", "drama").contains(genre)) {
            throw new IllegalArgumentException("Invalid genre: " + genre);
        }

        List<EventDto> eventDtos = eventService.getEventsByGenre(genre);
        return ApiResponseDto.success(HttpStatus.OK.value(), eventDtos);
    }
}
