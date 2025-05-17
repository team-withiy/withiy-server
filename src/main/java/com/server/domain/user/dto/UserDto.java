package com.server.domain.user.dto;

import org.springframework.beans.factory.annotation.Autowired;

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

    private static S3UrlConfig s3UrlConfig;

    public UserDto() {
        // Default constructor
    }

    @Autowired
    public void setS3UrlConfig(S3UrlConfig s3UrlConfig) {
        UserDto.s3UrlConfig = s3UrlConfig;
    }

    public static UserDto from(User user, Boolean isRegistered) {
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
                .build();
    }
}
