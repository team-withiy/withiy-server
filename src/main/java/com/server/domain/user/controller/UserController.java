package com.server.domain.user.controller;

import com.server.domain.user.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.server.domain.user.entity.User;
import com.server.domain.user.service.UserService;
import com.server.global.dto.ApiResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // 내 정보
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/me")
    @Operation(summary = "자기 정보 얻기", description = "로그인한 유저의 정보 반환")
    public ApiResponseDto<UserDto> getUser(@AuthenticationPrincipal User user) {
        UserDto userDto = userService.getUser(user);
        return ApiResponseDto.success(HttpStatus.OK.value(), userDto);
    }

    // 사용자 프로필 조회 (userCode로)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/profile/{userCode}")
    @Operation(summary = "사용자 프로필 조회", description = "userCode를 이용하여 사용자의 기본 프로필 정보 조회")
    public ApiResponseDto<UserProfileResponseDto> getUserProfile(@PathVariable String userCode) {
        UserProfileResponseDto userProfile = userService.getUserProfileByCode(userCode);
        return ApiResponseDto.success(HttpStatus.OK.value(), userProfile);
    }

    // 사용자 탈퇴
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/me")
    @Operation(summary = "유저 삭제(탈퇴)", description = "로그인한 유저 삭제")
    public ApiResponseDto<String> deleteUser(@AuthenticationPrincipal User user) {
        String name = userService.deleteUser(user, true);
        return ApiResponseDto.success(HttpStatus.OK.value(),
                String.format("Success delete user: %s", name));
    }

    // 사용자 등록
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/me")
    @Operation(summary = "유저 등록", description = "유저 약관 동의 및 닉네임 업데이트")
    public ApiResponseDto<String> registerUser(@AuthenticationPrincipal User user,
            @RequestBody RegisterUserInDto body) {
        String nickname =
                userService.registerUser(user, body.getTermAgreements(), body.getNickname());
        return ApiResponseDto.success(HttpStatus.OK.value(),
                String.format("User %s information updated successfully", nickname));
    }

    // 계정 관리 (복구 또는 삭제)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/restore")
    @Operation(summary = "계정 관리", description = "삭제된 계정 복구 또는 계정 삭제")
    public ApiResponseDto<String> manageAccount(@AuthenticationPrincipal User user,
            @RequestBody RestoreAccountDto body) {
        String nickname;
        String message;

        if (body.isRestore()) {
            // 계정 복구
            nickname = userService.restoreAccount(user.getId());
            message = String.format("User account %s restored successfully", nickname);
        } else {
            // 계정 삭제
            nickname = userService.deleteUser(user, false);
            message = String.format("User account %s deleted successfully", nickname);
        }

        return ApiResponseDto.success(HttpStatus.OK.value(), message);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "로그인한 유저 로그아웃")
    public ApiResponseDto<String> logout(@AuthenticationPrincipal User user) {

        // TODO: Redis에서 refresh token 관리
        userService.clearRefreshToken(user.getId());
        return ApiResponseDto.success(HttpStatus.OK.value(), "Logout successful");
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(value = "/profile")
    @Operation(summary = "프로필 업데이트",
        description = "사용자의 프로필을 업데이트합니다. 닉네임 및 프로필 이미지를 포함할 수 있습니다.")
    public ApiResponseDto<ProfileResponseDto> updateProfile(
        @AuthenticationPrincipal User user, @Valid @RequestBody ProfileUpdateDto requestDto) {

        log.info("Profile image update requested for user: {}", user.getNickname());

        ProfileResponseDto responseDto = userService.updateProfile(user, requestDto.getNickname(),
            requestDto.getThumbnail());
        return ApiResponseDto.success(HttpStatus.OK.value(), responseDto);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/notifications/settings")
    public ApiResponseDto<String> updateNotificationSettings(
        @AuthenticationPrincipal User user, @Valid @RequestBody NotificationSettingsDto requestDto) {

        log.info("Notification settings update requested for user: {}", user.getNickname());

        userService.updateNotificationSettings(user, requestDto);
        return ApiResponseDto.success(HttpStatus.OK.value(), "Notification settings updated successfully");
    }
}
