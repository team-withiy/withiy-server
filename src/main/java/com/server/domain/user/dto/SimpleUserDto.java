package com.server.domain.user.dto;

import com.server.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SimpleUserDto {

	@Schema(description = "사용자 ID", example = "6")
	private Long id;
	@Schema(description = "사용자 닉네임", example = "위디1호")
	private String nickname;
	@Schema(description = "프로필 이미지 URL", example = "https://cdn.example.com/user/6/uuid.jpg")
	private String thumbnail;

	@Builder
	public SimpleUserDto(Long id, String nickname, String thumbnail) {
		this.id = id;
		this.nickname = nickname;
		this.thumbnail = thumbnail;
	}

	public static SimpleUserDto from(User user) {
		return SimpleUserDto.builder()
			.id(user.getId())
			.nickname(user.getNickname())
			.thumbnail(user.getThumbnail())
			.build();
	}
}
