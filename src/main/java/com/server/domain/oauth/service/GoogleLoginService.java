package com.server.domain.oauth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.server.domain.oauth.dto.GoogleTokenOutDto;
import com.server.domain.oauth.dto.GoogleUserOutDto;
import com.server.domain.user.entity.User;
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
public class GoogleLoginService {

    private final UserService userService;
    private final JwtService jwtService;

    @Value("${spring.security.oauth2.client.backend-uri}")
    private String backendUri;
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;
    @Value("${spring.security.oauth2.client.registration.google.callback-path}")
    private String callbackPath;

    public String getRedirectUri(String state) {
        return String.format(
                "https://accounts.google.com/o/oauth2/v2/auth?client_id=%s&response_type=code&redirect_uri=%s%s&state=%s&scope=email profile",
                clientId, backendUri, callbackPath, state);
    }

    public TokenDto auth(String code) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<GoogleTokenOutDto> googleTokenResponse = restTemplate.exchange(
                    "https://oauth2.googleapis.com/token",
                    HttpMethod.POST,
                    getAccessToken(code),
                    GoogleTokenOutDto.class);
            String googleAccessToken = googleTokenResponse.getBody().getAccessToken();

            ResponseEntity<GoogleUserOutDto> googleUserResponse = restTemplate.exchange(
                    "https://www.googleapis.com/oauth2/v2/userinfo",
                    HttpMethod.GET,
                    getUserInfo(googleAccessToken),
                    GoogleUserOutDto.class);
            GoogleUserOutDto googleUser = googleUserResponse.getBody();
            log.info("Received user info. Username: " + googleUser.getName());

            User user = userService.loginOrRegister(googleUser);
            String accessToken = jwtService.createAccessToken(user.getNickname());
            String refreshToken = jwtService.createRefreshToken(user.getNickname());

            userService.saveRefreshToken(user.getNickname(), refreshToken);

            return new TokenDto(accessToken, refreshToken);
        } catch (Exception e) {
            log.error(e.toString());
            throw new AuthException(AuthErrorCode.OAUTH_PROCESS_ERROR);
        }

    }

    private HttpEntity<MultiValueMap<String, String>> getAccessToken(String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", code);
        params.add("redirect_uri", String.format(
                "%s%s",
                backendUri, callbackPath));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        return new HttpEntity<>(params, headers);
    }

    private HttpEntity<MultiValueMap<String, String>> getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        return new HttpEntity<>(headers);
    }

    // TODO: refresh는 AuthService 이런 곳에서 해야할듯
    // public TokenDto refresh(String refreshToken) {
    // if (!jwtService.validateToken(refreshToken)) {
    // throw new AuthException(AuthErrorCode.INVALID_REFRESH_TOKEN);
    // }
    // String username = jwtService.extractUsername(refreshToken)
    // .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
    // User user = userService.getUserWithPersonalInfo(username);
    // if (!refreshToken.equals(user.getRefreshToken())) {
    // throw new AuthException(AuthErrorCode.INVALID_REFRESH_TOKEN);
    // }
    // String newAccessToken = jwtService.createAccessToken(username);
    // return new TokenDto(newAccessToken, refreshToken);
    // }
}
