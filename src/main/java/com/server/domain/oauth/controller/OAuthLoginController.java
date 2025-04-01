package com.server.domain.oauth.controller;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.server.domain.oauth.service.GoogleLoginService;
import com.server.domain.oauth.service.NaverLoginService;
import com.server.global.dto.ApiResponseDto;
import com.server.global.dto.TokenDto;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/auth")
public class OAuthLoginController {

    private final GoogleLoginService googleLoginService;
    private final NaverLoginService naverLoginService;

    @Value("${spring.security.oauth2.client.frontend-uri}")
    private String frontendUri;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/state")
    @Operation(summary = "", description = "")
    public ApiResponseDto<String> state() {
        SecureRandom random = new SecureRandom();
        String state = new BigInteger(128, random).toString();
        return ApiResponseDto.success(HttpStatus.OK.value(), state);
    }

    @ResponseStatus(HttpStatus.FOUND)
    @GetMapping("/google")
    @Operation(summary = "", description = "")
    public ApiResponseDto<String> googleLogin(HttpServletResponse response, @RequestParam String state) {
        String url = googleLoginService.getRedirectUri(state);
        response.setHeader(HttpHeaders.LOCATION, url);
        return ApiResponseDto.success(HttpStatus.FOUND.value(), "Redirection for Login");
    }

    @ResponseStatus(HttpStatus.FOUND)
    @GetMapping("/google/callback")
    @Operation(summary = "", description = "")
    public ApiResponseDto<TokenDto> googleToken(HttpServletResponse response, @RequestParam String code) {
        TokenDto tokenDto = googleLoginService.auth(code);
        response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDto.getAccessToken());
        response.setHeader(HttpHeaders.LOCATION,
                String.format("%s/auth/callback?accessToken=%s&refreshToken=%s",
                        frontendUri, tokenDto.getAccessToken(), tokenDto.getRefreshToken()));
        return ApiResponseDto.success(HttpStatus.FOUND.value(), tokenDto);

    }

    @ResponseStatus(HttpStatus.FOUND)
    @GetMapping("/naver")
    @Operation(summary = "", description = "")
    public ApiResponseDto<String> naverLogin(HttpServletResponse response, @RequestParam String state)
            throws UnsupportedEncodingException {
        String url = naverLoginService.getRedirectUri(state);
        response.setHeader(HttpHeaders.LOCATION, url);
        return ApiResponseDto.success(HttpStatus.FOUND.value(), "Redirection for Login");
    }

    @ResponseStatus(HttpStatus.FOUND)
    @GetMapping("/naver/callback")
    @Operation(summary = "", description = "")
    public ApiResponseDto<TokenDto> naverToken(HttpServletResponse response, @RequestParam String code,
            @RequestParam String state) {
        TokenDto tokenDto = naverLoginService.auth(code, state);
        response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDto.getAccessToken());
        response.setHeader(HttpHeaders.LOCATION,
                String.format("%s/auth/callback?accessToken=%s&refreshToken=%s",
                        frontendUri, tokenDto.getAccessToken(), tokenDto.getRefreshToken()));
        return ApiResponseDto.success(HttpStatus.FOUND.value(), tokenDto);

    }

    // @ResponseStatus(HttpStatus.OK)
    // @PostMapping("/refresh")
    // @Operation(summary = "refresh 토큰 발급", description = "Token Dto을 입력받아 refresh
    // token 제공")
    // public ApiResponseDto<TokenDto> refreshToken(@RequestBody TokenDto tokenDto)
    // {
    // String refreshToken = tokenDto.getRefreshToken();
    // log.info(refreshToken);
    // TokenDto newTokenDto = googleLoginService.refresh(refreshToken);
    // return ApiResponseDto.success(HttpStatus.OK.value(), newTokenDto);
    // }
}
