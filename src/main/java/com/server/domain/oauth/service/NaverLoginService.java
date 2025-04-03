//package com.server.domain.oauth.service;
//
//import java.io.UnsupportedEncodingException;
//import java.net.URLEncoder;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.RestTemplate;
//
//import com.server.domain.oauth.dto.NaverTokenOutDto;
//import com.server.domain.oauth.dto.NaverUserOutDto;
//import com.server.domain.oauth.entity.OAuth;
//import com.server.domain.user.service.UserService;
//import com.server.global.dto.TokenDto;
//import com.server.global.error.code.AuthErrorCode;
//import com.server.global.error.exception.AuthException;
//import com.server.global.jwt.JwtService;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class NaverLoginService {
//
//    private final AuthService authService;
//    private final UserService userService;
//    private final JwtService jwtService;
//
//    @Value("${spring.security.oauth2.client.backend-uri}")
//    private String backendUri;
//    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
//    private String clientId;
//    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
//    private String clientSecret;
//    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
//    private String callbackPath;
//    @Value("${spring.security.oauth2.client.registration.kakao.authorization-grant-type}")
//    private String authorizationGrantType;
//    @Value("${spring.security.oauth2.client.provider.naver.authorization-uri}")
//    private String authorizationUri;
//    @Value("${spring.security.oauth2.client.provider.naver.token-uri}")
//    private String tokenUri;
//    @Value("${spring.security.oauth2.client.provider.naver.user-info-uri}")
//    private String userInfoUri;
//
//    public String getRedirectUri(String state) throws UnsupportedEncodingException {
//        String encodedCallbackPath = URLEncoder.encode(String.format("%s%s", backendUri, callbackPath), "UTF-8");
//        return String.format(
//                "%s?response_type=code&client_id=%s&redirect_uri=%s&state=%s",
//                authorizationUri, clientId, encodedCallbackPath, state);
//    }
//
//    public TokenDto auth(String code, String state) {
//        try {
//
//            RestTemplate restTemplate = new RestTemplate();
//            ResponseEntity<NaverTokenOutDto> naverTokenResponse = restTemplate.exchange(
//                    tokenUri,
//                    HttpMethod.POST,
//                    getAccessToken(code, state),
//                    NaverTokenOutDto.class);
//            String naverAccessToken = naverTokenResponse.getBody().getAccessToken();
//
//            ResponseEntity<NaverUserOutDto> naverUserResponse = restTemplate.exchange(
//                    userInfoUri,
//                    HttpMethod.GET,
//                    getUserInfo(naverAccessToken),
//                    NaverUserOutDto.class);
//            NaverUserOutDto naverUser = naverUserResponse.getBody();
//            log.info("Received user info. Username: " + naverUser.getResponse().getNickname());
//
//            OAuth oAuth = authService.loginOrRegister(naverUser);
//            Long userId = oAuth.getUser().getId();
//            String accessToken = jwtService.createAccessToken(userId);
//            String refreshToken = jwtService.createRefreshToken(userId);
//            userService.saveRefreshToken(userId, refreshToken);
//            return new TokenDto(accessToken, refreshToken);
//        } catch (Exception e) {
//            log.error(e.toString());
//            throw new AuthException(AuthErrorCode.OAUTH_PROCESS_ERROR);
//        }
//    }
//
//    private HttpEntity<MultiValueMap<String, String>> getAccessToken(String code, String state) {
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("grant_type", authorizationGrantType);
//        params.add("client_id", clientId);
//        params.add("client_secret", clientSecret);
//        params.add("code", code);
//        params.add("state", state);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//        return new HttpEntity<>(params, headers);
//    }
//
//    private HttpEntity<MultiValueMap<String, String>> getUserInfo(String accessToken) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBearerAuth(accessToken);
//        return new HttpEntity<>(headers);
//    }
//}
