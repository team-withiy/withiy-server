package com.server.domain.oauth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoUserOutDto {
    @JsonProperty("id")
    private String id;

    @JsonProperty("kakao_account")
    private KakaoUserAccount account;

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KakaoUserAccount{
        @JsonProperty("profile")
        private KakaoUserProfile kakaoUserProfile;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KakaoUserProfile{
        private String nickname;
        @JsonProperty("profile_image_url")
        private String profileImage;
    }
}
