package com.server.domain.user.dto;

import org.springframework.beans.factory.annotation.Autowired;

import com.server.domain.user.entity.User;
import com.server.global.config.S3UrlConfig;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponseDto {

    @Schema(description = "사용자 고유 코드", example = "ABC123")
    private String userCode;

    @Schema(description = "사용자 닉네임", example = "사용자닉네임")
    private String nickname;

    @Schema(description = "프로필 이미지 URL",
            example = "https://cdn.withiy.com/users/profile/abc123.jpg")
    private String profileImageUrl;

    @Schema(description = "자기소개", example = "안녕하세요!")
    private String bio;

    private static S3UrlConfig s3UrlConfig;

    @Autowired
    public void setS3UrlConfig(S3UrlConfig s3UrlConfig) {
        UserProfileResponseDto.s3UrlConfig = s3UrlConfig;
    }

    public static UserProfileResponseDto from(User user) {
        // 썸네일 URL이 S3 URL인 경우 CloudFront URL로 변환
        String profileImageUrl = user.getThumbnail();
        if (profileImageUrl != null && s3UrlConfig != null
                && profileImageUrl.contains(s3UrlConfig.getS3Url())) {
            profileImageUrl =
                    profileImageUrl.replace(s3UrlConfig.getS3Url(), s3UrlConfig.getCloudfrontUrl());
        }

        return UserProfileResponseDto.builder().userCode(user.getCode())
                .nickname(user.getNickname()).profileImageUrl(profileImageUrl).bio(null) // User
                                                                                         // 엔티티에 bio
                                                                                         // 필드가 없으므로
                                                                                         // 일단 null로
                                                                                         // 설정
                .build();
    }
}
