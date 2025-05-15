package com.server.domain.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.server.domain.user.dto.ProfileImageResponseDto;
import com.server.domain.user.entity.User;
import com.server.domain.user.service.UserService;
import com.server.global.dto.ApiResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/users")
public class UserProfileController {

    private final UserService userService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "프로필 이미지 업데이트",
            description = "사용자의 프로필 이미지를 업데이트합니다. AWS S3에 이미지가 업로드됩니다.")
    public ApiResponseDto<ProfileImageResponseDto> updateProfileImage(
            @AuthenticationPrincipal User user, @RequestPart("file") MultipartFile file) {

        log.info("Profile image update requested for user: {}", user.getNickname());

        if (file.isEmpty()) {
            log.warn("Empty file received for profile image update");
            return ApiResponseDto.error(HttpStatus.BAD_REQUEST.value(), "Empty file provided");
        }

        // 파일 크기 검증 (5MB 제한)
        if (file.getSize() > 5 * 1024 * 1024) {
            log.warn("File too large: {} bytes", file.getSize());
            return ApiResponseDto.error(HttpStatus.BAD_REQUEST.value(),
                    "File size exceeds 5MB limit");
        }

        // 파일 형식 검증
        String contentType = file.getContentType();
        if (contentType == null
                || !(contentType.equals("image/jpeg") || contentType.equals("image/png")
                        || contentType.equals("image/jpg") || contentType.equals("image/gif"))) {
            log.warn("Invalid file type: {}", contentType);
            return ApiResponseDto.error(HttpStatus.BAD_REQUEST.value(),
                    "Only JPEG, PNG, JPG and GIF images are allowed");
        }

        ProfileImageResponseDto responseDto = userService.updateProfileImage(user, file);
        return ApiResponseDto.success(HttpStatus.OK.value(), responseDto);
    }
}
