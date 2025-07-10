package com.server.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateDto {

    @Schema(description = "새 닉네임", example = "위디2호")
    private String nickname;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/images/profile.png")
    private String thumbnail;
}
