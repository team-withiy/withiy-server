package com.server.domain.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.server.domain.user.dto.GetUserOutDto;
import com.server.domain.user.dto.RegisterUserInDto;
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
    public ApiResponseDto<GetUserOutDto> getUser(@AuthenticationPrincipal User user) {
        GetUserOutDto userDto = userService.getUser(user);
        return ApiResponseDto.success(HttpStatus.OK.value(), userDto);
    }

    // 사용자 탈퇴
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/me")
    @Operation(summary = "유저 삭제(탈퇴)", description = "로그인한 유저 삭제")
    public ApiResponseDto<String> deleteUser(@AuthenticationPrincipal User user) {
        String name = userService.deleteUser(user);
        return ApiResponseDto.success(HttpStatus.OK.value(),
                String.format("Success delete user: %s", name));
    }

    // 사용자 등록
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/me")
    @Operation(summary = "유저 등록", description = "유저의 isRegister를 true로 설정")
    public ApiResponseDto<String> registerUser(@AuthenticationPrincipal User user,
            @RequestBody RegisterUserInDto body) {
        userService.registerUser(user, body.getNickname());
        return ApiResponseDto.success(HttpStatus.OK.value(), "User registered successfully");
    }
}
