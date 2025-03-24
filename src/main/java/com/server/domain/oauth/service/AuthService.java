
package com.server.domain.oauth.service;

import org.springframework.stereotype.Service;

import com.server.domain.oauth.dto.GoogleUserOutDto;
import com.server.domain.oauth.entity.OAuth;
import com.server.domain.oauth.enums.SocialType;
import com.server.domain.oauth.repository.OAuthRepository;
import com.server.domain.user.entity.User;
import com.server.domain.user.service.UserService;
import com.server.global.dto.TokenDto;
import com.server.global.error.code.AuthErrorCode;
import com.server.global.error.code.UserErrorCode;
import com.server.global.error.exception.AuthException;
import com.server.global.error.exception.BusinessException;
import com.server.global.jwt.JwtService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final OAuthRepository oAuthRepository;
    private final UserService userService;
    private final JwtService jwtService;

    @Transactional
    public OAuth loginOrRegister(GoogleUserOutDto user) {
        return oAuthRepository.findBySocialTypeAndAuthId(SocialType.GOOGLE, user.getId())
                .orElseGet(() -> registerNewUser(user));
    }

    private OAuth registerNewUser(GoogleUserOutDto user) {
        User newUser = User.builder()
                .nickname(user.getName())
                .thumbnail(user.getPicture())
                .build();
        OAuth oAuth = OAuth.builder()
                .socialType(SocialType.GOOGLE)
                .authId(user.getId())
                .email(user.getEmail())
                .user(newUser)
                .build();
        return oAuthRepository.save(oAuth);
    }

    public TokenDto refresh(String refreshToken) {
        if (!jwtService.validateToken(refreshToken)) {
            throw new AuthException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }
        Long userId = jwtService.extractUserId(refreshToken)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
        User user = userService.getUserWithPersonalInfo(userId);
        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new AuthException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }
        String newAccessToken = jwtService.createAccessToken(userId);
        return new TokenDto(newAccessToken, refreshToken);
    }
}
