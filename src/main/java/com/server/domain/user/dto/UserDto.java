package com.server.domain.user.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.server.domain.user.entity.User;
import com.server.global.config.S3UrlConfig;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@AllArgsConstructor
public class UserDto {
    @Schema(description = "사용자 닉네임", example = "위디1호")
    private String nickname;
    @Schema(description = "프로필 이미지 URL", example = "https://cdn.example.com/user/6/uuid.jpg")
    private String thumbnail;
    @Schema(description = "복구 가능 여부", example = "true")
    private Boolean restoreEnabled;
    @Schema(description = "약관 동의 여부", example = "true")
    private Boolean isRegistered;
    @Schema(description = "사용자 고유 코드", example = "aB3jK2M8p9cR1Vw_K0Nxug")
    private String code;
    @Schema(description = "커플 여부", example = "true")
    private Boolean hasCouple;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "커플 정보", nullable = true)
    private CoupleDto couple;
    public UserDto() {
        // Default constructor
    }

    public static UserDto from(User user, boolean isRegistered, CoupleDto couple, S3UrlConfig s3UrlConfig) {
        boolean restoreEnabled;
        restoreEnabled = user.getDeletedAt() != null;

        // 썸네일 URL이 S3 URL인 경우 CloudFront URL로 변환
        String thumbnailUrl = user.getThumbnail();
        if (thumbnailUrl != null && s3UrlConfig != null
                && thumbnailUrl.contains(s3UrlConfig.getS3Url())) {
            thumbnailUrl =
                    thumbnailUrl.replace(s3UrlConfig.getS3Url(), s3UrlConfig.getCloudfrontUrl());
        }

        return UserDto.builder().nickname(user.getNickname()).thumbnail(thumbnailUrl)
            .restoreEnabled(restoreEnabled).isRegistered(isRegistered).code(user.getCode())
            .hasCouple(couple != null).couple(couple)
            .build();
    }
}
