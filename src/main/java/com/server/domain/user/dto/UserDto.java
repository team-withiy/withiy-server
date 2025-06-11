package com.server.domain.user.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.server.domain.user.entity.User;
import com.server.global.config.S3UrlConfig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserDto {
    private String nickname;
    private String thumbnail;
    private Boolean restoreEnabled;
    private Boolean isRegistered;
    private String code;
    private Boolean hasCouple;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private CoupleDto couple;

    public UserDto() {
        // Default constructor
    }

    public static UserDto from(User user, Boolean isRegistered, CoupleDto couple, S3UrlConfig s3UrlConfig) {
        Boolean restoreEnabled;
        if (user.getDeletedAt() != null) {
            restoreEnabled = true;
        } else {
            restoreEnabled = false;
        }

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
