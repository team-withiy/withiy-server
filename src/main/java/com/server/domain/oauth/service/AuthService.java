package com.server.domain.oauth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import com.server.domain.user.entity.User;
import com.server.domain.user.repository.UserRepository;
import com.server.domain.user.service.UserService;
import com.server.global.dto.TokenDto;
import com.server.global.error.code.AuthErrorCode;
import com.server.global.error.exception.AuthException;
import com.server.global.jwt.JwtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    @Value("${spring.security.oauth2.client.frontend-uri}")
    private String frontendUri;

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserService userService;

    public String getRedirectUri(String accessToken, String refreshToken) {
        return UriComponentsBuilder.fromUriString(String.format("%s/auth/callback", frontendUri))
                .queryParam("accessToken", accessToken).queryParam("refreshToken", refreshToken)
                .build().toUriString();
    }

    /**
     * 리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급합니다.
     *
     * @param refreshToken 리프레시 토큰
     * @return 새로운 액세스 토큰과 리프레시 토큰
     * @throws AuthException 리프레시 토큰이 유효하지 않거나 만료된 경우
     */
    @Transactional
    public TokenDto refreshToken(String refreshToken) {
        // 리프레시 토큰 검증
        if (!jwtService.validateToken(refreshToken)) {
            throw new AuthException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 토큰에서 사용자 ID 추출
        Long userId = jwtService.extractUserId(refreshToken)
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_REFRESH_TOKEN));

        // 사용자 조회 및 저장된 리프레시 토큰 일치 여부 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_REFRESH_TOKEN));

        // 저장된 리프레시 토큰과 일치하는지 확인
        if (user.getRefreshToken() == null || !user.getRefreshToken().equals(refreshToken)) {
            throw new AuthException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 새로운 토큰 발급
        String newAccessToken = jwtService.createAccessToken(userId);
        String newRefreshToken = jwtService.createRefreshToken(userId);

        // 새로운 리프레시 토큰 저장
        userService.saveRefreshToken(userId, newRefreshToken);

        log.info("Token refreshed for user ID: {}", userId);
        return new TokenDto(newAccessToken, newRefreshToken);
    }
}
