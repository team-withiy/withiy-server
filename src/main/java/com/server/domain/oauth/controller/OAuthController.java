package com.server.domain.oauth.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.server.domain.oauth.service.AuthService;
import com.server.global.dto.ApiResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/auth")
public class OAuthController {

    private final AuthService authService;

    @ResponseStatus(HttpStatus.FOUND)
    @GetMapping("/success")
    @Operation(hidden = true)
    public ApiResponseDto<String> getRedirect(@RequestParam String accessToken, @RequestParam String refreshToken,
            HttpServletResponse response) throws IOException {
        String redirectUri = authService.getRedirectUri(accessToken, refreshToken);
        response.sendRedirect(redirectUri);
        return ApiResponseDto.success(HttpStatus.FOUND.value(), redirectUri);
    }
}
