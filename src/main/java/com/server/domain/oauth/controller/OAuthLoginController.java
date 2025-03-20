package com.server.domain.oauth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.server.domain.oauth.service.OAuthLoginService;
import com.server.global.dto.ApiResponseDto;
import com.server.global.dto.TokenDto;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class OAuthLoginController {

    private final OAuthLoginService oAuthLoginService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/refresh")
    @Operation(summary = "refresh 토큰 발급", description = "Token Dto을 입력받아 refresh token 제공")
    public ApiResponseDto<TokenDto> refreshToken(@RequestBody TokenDto tokenDto) {
        String refreshToken = tokenDto.getRefreshToken();
        log.info(refreshToken);

        TokenDto newTokenDto = oAuthLoginService.refresh(refreshToken);

        return ApiResponseDto.success(HttpStatus.OK.value(), newTokenDto);
    }

}
