package com.server.domain.place.controller;

import com.server.domain.place.dto.*;
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


    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/admin")
    @Operation(summary = "장소 생성 api", description = "관리자가 장소 등록할 수 있는 api, 하위카테고리 선택")
    public ApiResponseDto<PlaceDto> createPlace(@RequestBody CreatePlaceDto createPlaceDto) {
        PlaceDto placeDto = placeService.createPlace(createPlaceDto);
        return ApiResponseDto.success(HttpStatus.OK.value(), placeDto);
    }

    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/create")
    @Operation(summary = "사용자 장소 최초 등록 api", description = "사용자가 처음으로 등록하는 장소 api")
    public ApiResponseDto<PlaceDto> createPlaceFirst(@AuthenticationPrincipal User user, @RequestBody CreatePlaceByUserDto createPlaceByUserDto) {
        PlaceDto placeDto = placeService.createPlaceFirst(user, createPlaceByUserDto);
        return ApiResponseDto.success(HttpStatus.OK.value(), placeDto);
    }

    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    @Operation(summary = "사용자 장소 등록 api", description = "등록되어 있는 장소에 대한 사진/리뷰 저장하는 api")
    public ApiResponseDto<PlaceDto> registerPlace(@AuthenticationPrincipal User user, @RequestBody RegisterPlaceDto registerPlaceDto) {
        PlaceDto placeDto = placeService.registerPlace(user, registerPlaceDto);
        return ApiResponseDto.success(HttpStatus.OK.value(), placeDto);
    }



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
            placeDto = placeService.getPlaceSimpleDetail(placeId);
        }else {
            placeDto = placeService.getPlaceSimpleDetailAfterLogin(placeId, user.getId());
        }return ApiResponseDto.success(HttpStatus.OK.value(), placeDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/detail/{placeId}")
    @Operation(summary = "특정 장소 상세 정보 가져오기", description = "장소 id를 받아 특정 장소 자세한 정보 조회")
    public ApiResponseDto<PlaceDetailDto> getPlaceDetail(@PathVariable Long placeId, @AuthenticationPrincipal User user){
        PlaceDetailDto placeDetailDto;
        if(user==null){
            placeDetailDto = placeService.getPlaceDetail(placeId);
        }else {
            placeDetailDto = placeService.getPlaceDetailAfterLogin(placeId, user.getId());
        }return ApiResponseDto.success(HttpStatus.OK.value(), placeDetailDto);

    }
}
