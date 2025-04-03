package com.server.domain.oauth.dto;

import com.server.domain.oauth.entity.OAuth;
import com.server.domain.user.entity.User;
import com.server.global.error.exception.AuthException;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static com.server.global.error.code.AuthErrorCode.ILLEGAL_REGISTRATION_ID;

@Getter
@Builder
@Slf4j
public class OAuth2UserInfo{
    private Map<String, Object> attributes;

    private String nickname;
    private String email;
    private String picture;
    private String provider;
    private String providerId;


    public static OAuth2UserInfo of(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId) { // registration id별로 userInfo 생성
            case "google" -> ofGoogle(registrationId, attributes);
            case "kakao" -> ofKakao(attributes);
            default -> throw new AuthException(ILLEGAL_REGISTRATION_ID);
        };
    }

    private static OAuth2UserInfo ofGoogle(String registrationId, Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
                .nickname((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .provider(registrationId)
                .attributes(attributes)
                .providerId((String) attributes.get("sub"))
                .build();
    }

    private static OAuth2UserInfo ofKakao(Map<String, Object> attributes) {
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) account.get("profile");

        return OAuth2UserInfo.builder()
                .nickname((String) profile.get("nickname"))
                .email((String) account.get("email"))
                .picture((String) profile.get("profile_image_url"))
                .build();
    }

    public OAuth toEntity() {
        User user = User.builder()
                .nickname(nickname)
                .thumbnail(picture)
                .build();
        return OAuth.builder()
                .provider(provider)
                .providerId(providerId)
                .email(email)
                .user(user)
                .build();

    }


}