package com.server.domain.oauth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NaverUserOutDto {

    @JsonProperty("response")
    private NaverUserResponse response;

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NaverUserResponse {
        private String id;
        private String email;
        private String nickname;

        @JsonProperty("profile_image")
        private String profileImage;
    }
}