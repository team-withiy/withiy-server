package com.server.domain.place.controller;

import com.server.domain.place.dto.PlaceDetailDto;
import com.server.domain.place.dto.PlaceImageDto;
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
    public ApiResponseDto<PlaceDetailDto> getPlaceDetail(@PathVariable Long placeId) {
        PlaceDetailDto placeDetailDto = placeService.getPlaceDetail(placeId);
        return ApiResponseDto.success(HttpStatus.OK.value(), placeDetailDto);
    }

    /**
     * 장소 이미지 업로드 API
     */
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/{placeId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "장소 이미지 업로드", description = "장소에 새로운 이미지 업로드")
    public ApiResponseDto<PlaceImageDto> uploadPlaceImage(@AuthenticationPrincipal User user,
            @PathVariable Long placeId, @RequestPart("file") MultipartFile file) {

        log.info("Place image upload requested for place ID: {}, by user: {}", placeId,
                user.getNickname());

        PlaceImageDto placeImageDto = placeService.uploadPlaceImage(placeId, file);
        return ApiResponseDto.success(HttpStatus.OK.value(), placeImageDto);
    }

    /**
     * 장소 대표 이미지(썸네일) 업데이트 API
     */
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/{placeId}/thumbnail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "장소 대표 이미지 업데이트", description = "장소의 대표 이미지(썸네일) 업데이트")
    public ApiResponseDto<PlaceDetailDto> updatePlaceThumbnail(@AuthenticationPrincipal User user,
            @PathVariable Long placeId, @RequestPart("file") MultipartFile file) {

        log.info("Place thumbnail update requested for place ID: {}, by user: {}", placeId,
                user.getNickname());

        PlaceDetailDto placeDetailDto = placeService.updatePlaceThumbnail(placeId, file);
        return ApiResponseDto.success(HttpStatus.OK.value(), placeDetailDto);
    }
}
