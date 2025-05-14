package com.server.domain.place.controller;

import com.server.domain.place.dto.PlaceDetailDto;
import com.server.domain.place.service.PlaceService;
import com.server.global.dto.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/places")
public class PlaceController {
    private final PlaceService placeService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{placeId}")
    @Operation(summary = "장소 상세 정보", description = "해당 id 장소 상제 정보 PlaceDetailDto 반환")
    public ApiResponseDto<PlaceDetailDto> getPlaceDetail(@RequestParam Long placeId){
        PlaceDetailDto placeDetailDto = placeService.getPlaceDetail(placeId);
        return ApiResponseDto.success(HttpStatus.OK.value(), placeDetailDto);
    }
}
