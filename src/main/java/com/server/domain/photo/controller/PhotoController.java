package com.server.domain.photo.controller;

import com.server.domain.photo.dto.PhotoDto;
import com.server.domain.photo.service.PhotoService;
import com.server.domain.place.dto.CreatePlaceDto;
import com.server.domain.place.dto.PlaceDto;
import com.server.domain.user.entity.User;
import com.server.global.dto.ApiResponseDto;
import com.server.global.dto.ImageResponseDto;
import com.server.global.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/photos")
public class PhotoController {

    private final ImageService imageService;
    private final PhotoService photoService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/{isPrivate}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "사진 저장 api", description = "앨범을 위한 사진 등록 api, 공개 여부 필요")
    public ApiResponseDto<List<PhotoDto>> uploadMultipleImages(
            @AuthenticationPrincipal User user,
            @PathVariable boolean isPrivate,
            @RequestPart("files") List<MultipartFile> files,
            @RequestParam("entityType") String entityType,
            @RequestParam(value = "entityId", required = false) Long entityId) {

        log.info("Uploading multiple images: count={}, entityType={}, entityId={}, by user={}",
                files.size(), entityType, entityId, user.getNickname());

        try {
            List<ImageResponseDto> results = imageService.uploadImages(files, entityType, entityId);
            List<Integer> sequences = new ArrayList<>();
            for (int i = 0; i < results.size(); i++) {
                sequences.add(i); // 0부터 시작하는 순서
            }

            List<PhotoDto> photoDtos = photoService.convertToPhotoDtos(results, isPrivate, sequences);
            return ApiResponseDto.success(HttpStatus.OK.value(), photoDtos);

        } catch (IllegalArgumentException e) {
            return ApiResponseDto.error(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        } catch (Exception e) {
            log.error("Error uploading multiple images", e);
            return ApiResponseDto.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "이미지 업로드 실패: " + e.getMessage());
        }
    }

}
