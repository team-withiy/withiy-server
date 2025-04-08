package com.server.domain.oauth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class AuthService {
    @Value("${spring.security.oauth2.client.frontend-uri}")
    private String frontendUri;

    public String getRedirectUri(String accessToken, String refreshToken) {
        return UriComponentsBuilder.fromUriString(frontendUri).queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken).build().toUriString();

    }
}
