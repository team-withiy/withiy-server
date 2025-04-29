package com.server.domain.oauth.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.server.domain.oauth.dto.RefreshTokenDto;
import com.server.domain.oauth.service.AuthService;
import com.server.global.dto.ApiResponseDto;
import com.server.global.dto.TokenDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "인증 관련 API")
public class OAuthController {

    private final AuthService authService;

    @ResponseStatus(HttpStatus.FOUND)
    @GetMapping("/success")
    @Operation(hidden = true)
    public ApiResponseDto<String> getRedirect(@RequestParam String accessToken,
            @RequestParam String refreshToken, HttpServletResponse response) throws IOException {
        String redirectUri = authService.getRedirectUri(accessToken, refreshToken);
        response.sendRedirect(redirectUri);
        return ApiResponseDto.success(HttpStatus.FOUND.value(), redirectUri);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/refresh")
    @Operation(summary = "토큰 갱신", description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급합니다.")
    public ApiResponseDto<TokenDto> refreshToken(@RequestBody RefreshTokenDto refreshTokenDto) {
        TokenDto tokenDto = authService.refreshToken(refreshTokenDto.getRefreshToken());
        return ApiResponseDto.success(HttpStatus.OK.value(), tokenDto);
    }
}
