package com.server.domain.user.dto;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.server.domain.user.entity.Couple;
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
public class CoupleDto {

    @Schema(description = "커플 고유 ID", example = "1")
    private Long id;

    @Schema(description = "파트너 닉네임", example = "파트너닉네임")
    private String partnerNickname;

    @Schema(description = "파트너 프로필 이미지", example = "https://example.com/profile.jpg")
    private String partnerThumbnail;

    @Schema(description = "처음 만난 날짜", example = "2025-01-01")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate firstMetDate;

    @Schema(description = "연결된 날짜", example = "2025-04-29")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate connectedDate;

    // 현재 사용자 기준으로 파트너 정보를 반환하는 팩토리 메서드
    public static CoupleDto from(Couple couple, User currentUser, S3UrlConfig s3UrlConfig) {
        User partner = couple.getUser1().getId().equals(currentUser.getId()) ? couple.getUser2()
                : couple.getUser1();

        // 썸네일 URL이 S3 URL인 경우 CloudFront URL로 변환
        String thumbnailUrl = partner.getThumbnail();
        if (thumbnailUrl != null && s3UrlConfig != null
                && thumbnailUrl.contains(s3UrlConfig.getS3Url())) {
            thumbnailUrl =
                    thumbnailUrl.replace(s3UrlConfig.getS3Url(), s3UrlConfig.getCloudfrontUrl());
        }

        return CoupleDto.builder().id(couple.getId()).partnerNickname(partner.getNickname())
                .partnerThumbnail(thumbnailUrl).firstMetDate(couple.getFirstMetDate())
                .connectedDate(couple.getCreatedAt().toLocalDate()).build();
    }
}
