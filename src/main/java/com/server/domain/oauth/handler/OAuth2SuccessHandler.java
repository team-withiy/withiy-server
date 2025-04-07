package com.server.domain.oauth.handler;

import java.io.IOException;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.server.domain.oauth.entity.OAuth;
import com.server.domain.oauth.repository.OAuthRepository;
import com.server.domain.user.service.UserService;
import com.server.global.jwt.JwtService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final OAuthRepository oAuthRepository;
    private final UserService userService;

    private static final String URI = "/auth/success";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {

        // OAuth2User에서 사용자 ID 추출
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Long userId = extractUserId(oAuth2User);
        log.info("userId " + userId);

        log.info("accesstoken 발급");
        // accessToken, refreshToken 발급
        String accessToken = jwtService.createAccessToken(userId);
        String refreshToken = jwtService.createRefreshToken(userId);

        userService.saveRefreshToken(userId, refreshToken);

        // 토큰 전달을 위한 redirect
        String redirectUrl =
                UriComponentsBuilder.fromUriString(URI).queryParam("accessToken", accessToken)
                        .queryParam("refreshToken", refreshToken).build().toUriString();

        response.sendRedirect(redirectUrl);
    }

    // OAuth2User에서 userId를 추출하는 메서드
    private Long extractUserId(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 제공자 타입 확인
        String provider = determineProvider(attributes);

        // 제공자 ID 추출
        String providerId = null;
        if ("google".equals(provider)) {
            providerId = (String) attributes.get("sub");
        } else if ("naver".equals(provider)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            providerId = (String) response.get("id");
        } else if ("kakao".equals(provider)) {
            providerId = String.valueOf(attributes.get("id"));
        }

        if (provider != null && providerId != null) {
            OAuth oAuth = oAuthRepository.findByProviderAndProviderId(provider, providerId)
                    .orElseThrow(EntityNotFoundException::new);
            Long userId = oAuth.getUser().getId();
            log.info("userId: " + userId);
            return userId;
        }

        return Long.valueOf(oAuth2User.getName());
    }

    // 속성에서 제공자 타입 결정하는 메서드
    private String determineProvider(Map<String, Object> attributes) {
        // Google의 경우 sub 필드가 있음
        if (attributes.containsKey("sub")) {
            return "google";
        }
        // Kakao의 경우 kakao_account 필드가 있음
        else if (attributes.containsKey("kakao_account")) {
            return "kakao";
        }
        // Naver의 경우 response 객체 안에 id가 있음
        else if (attributes.containsKey("response")) {
            return "naver";
        }

        return null; // 알 수 없는 제공자
    }

}
