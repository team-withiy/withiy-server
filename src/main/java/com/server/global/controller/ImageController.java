package com.server.global.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.server.domain.user.entity.User;
import com.server.global.dto.ApiResponseDto;
import com.server.global.dto.ImageResponseDto;
import com.server.global.service.ImageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 공통 이미지 업로드 API 컨트롤러
 */
@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@Slf4j
public class ImageController {

    private final ImageService imageService;

    /**
     * 이미지 업로드 API
     * 
     * @param user 인증된 사용자
     * @param file 업로드할 이미지 파일
     * @param entityType 이미지를 사용할 엔티티 타입 (예: "user", "place", "course")
     * @param entityId 엔티티의 ID (선택 사항, null 가능)
     * @return 업로드된 이미지 정보
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponseDto<ImageResponseDto> uploadImage(@AuthenticationPrincipal User user,
            @RequestPart("file") MultipartFile file, @RequestParam("entityType") String entityType,
            @RequestParam(value = "entityId", required = false) Long entityId) {

        log.info("Image upload requested for entity type: {}, entity ID: {}, by user: {}",
                entityType, entityId, user.getNickname());

        if (!imageService.validateImage(file)) {
            String errorMessage = imageService.getValidationErrorMessage(file);
            log.warn("Image validation failed: {}", errorMessage);
            return ApiResponseDto.error(HttpStatus.BAD_REQUEST.value(), errorMessage);
        }

        try {
            ImageResponseDto responseDto = imageService.uploadImage(file, entityType, entityId);
            return ApiResponseDto.success(HttpStatus.OK.value(), responseDto);
        } catch (Exception e) {
            log.error("Failed to upload image", e);
            return ApiResponseDto.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Failed to upload image: " + e.getMessage());
        }
    }

    /**
     * 이미지 삭제 API
     * 
     * @param imageUrl 삭제할 이미지의 URL (인코딩된 URL)
     * @return 삭제 결과
     */
    @DeleteMapping("/{imageUrl}")
    public ApiResponseDto<Void> deleteImage(@AuthenticationPrincipal User user,
            @PathVariable String imageUrl) {

        log.info("Image deletion requested for URL: {}, by user: {}", imageUrl, user.getNickname());

        try {
            imageService.deleteImage(imageUrl);
            return ApiResponseDto.success(HttpStatus.OK.value(), null);
        } catch (Exception e) {
            log.error("Failed to delete image", e);
            return ApiResponseDto.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Failed to delete image: " + e.getMessage());
        }
    }
}
