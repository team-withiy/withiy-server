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
    private String nameAttributeKey;
    private String nickname;
    private String email;
    private String picture;
    private String provider;
    private String providerId;


    public static OAuth2UserInfo of(String registrationId, String nameAttributeKey, Map<String, Object> attributes) {
        return switch (registrationId) { // registration id별로 userInfo 생성
            case "google" -> ofGoogle(registrationId,nameAttributeKey ,attributes);
            case "naver" -> ofNaver(registrationId, nameAttributeKey, attributes);
            case "kakao" -> ofKakao(registrationId, nameAttributeKey, attributes);
            default -> throw new AuthException(ILLEGAL_REGISTRATION_ID);
        };
    }

    private static OAuth2UserInfo ofGoogle(String registrationId,String nameAttributeKey ,Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
                .nameAttributeKey(nameAttributeKey)
                .nickname((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .provider(registrationId)
                .attributes(attributes)
                .providerId((String) attributes.get("sub"))
                .build();
    }

    private static OAuth2UserInfo ofNaver(String registrationId,String nameAttributeKey ,Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuth2UserInfo.builder()
                .nameAttributeKey(nameAttributeKey)
                .nickname((String) response.get("nickname"))
                .email((String) response.get("email"))
                .picture((String) response.get("profile_image"))
                .provider(registrationId)
                .attributes(attributes)
                .providerId((String) response.get("id"))
                .build();
    }

    private static OAuth2UserInfo ofKakao(String registrationId,String nameAttributeKey ,Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");


        String providerId = String.valueOf(attributes.get("id"));


        String email = kakaoAccount.containsKey("email") ? (String) kakaoAccount.get("email") : null;

        return OAuth2UserInfo.builder()
                .nameAttributeKey(nameAttributeKey)
                .nickname((String) profile.get("nickname"))
                .email(email)
                .picture((String) profile.get("profile_image_url"))
                .provider(registrationId)
                .attributes(attributes)
                .providerId(providerId)
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