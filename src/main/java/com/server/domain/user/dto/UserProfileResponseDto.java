package com.server.domain.user.dto;

import com.server.domain.user.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
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

    private Boolean hasCouple;

    public static UserProfileResponseDto from(User user) {
        return UserProfileResponseDto.builder()
                .userCode(user.getCode())
                .nickname(user.getNickname())
                .profileImageUrl(user.getThumbnail())
                .hasCouple(user.getCouple() != null)
                .build();
    }
}
