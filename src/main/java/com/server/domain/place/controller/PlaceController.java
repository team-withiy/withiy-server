package com.server.domain.place.controller;

import com.server.domain.place.dto.PlaceDto;
import com.server.domain.place.dto.PlaceFocusDto;
import com.server.domain.place.service.PlaceService;
import com.server.domain.user.entity.User;
import com.server.global.dto.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/places")
public class PlaceController {
    private final PlaceService placeService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/focus")
    @Operation(summary = "지도 위 포커스 화면 장소 조회", description = "sw(남서쪽), ne(북동쪽) longitude, latitude 정보를 받아 그 사이에 있는 장소 정보 조회")
    public ApiResponseDto<List<PlaceFocusDto>> getMapFocusPlaces(@RequestParam String swLat, @RequestParam String swLng, @RequestParam String neLat, @RequestParam String neLng) {
        List<PlaceFocusDto> placeFocusDtos = placeService.getMapFocusPlaces(swLat, swLng, neLat, neLng);
        return ApiResponseDto.success(HttpStatus.OK.value(), placeFocusDtos);
    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{placeId}")
    @Operation(summary = "특정 장소 정보 가져오기", description = "장소 id를 받아 특정 장소 간단한 정보 조회")
    public ApiResponseDto<PlaceDto> getMapFocusPlaces(@PathVariable Long placeId, @AuthenticationPrincipal User user) {
        PlaceDto placeDto;
        if(user==null){
            placeDto = placeService.getPlaceDetail(placeId);
        }else {
            placeDto = placeService.getPlaceDetailAfterLogin(placeId, user.getId());
        }return ApiResponseDto.success(HttpStatus.OK.value(), placeDto);
    }


}
