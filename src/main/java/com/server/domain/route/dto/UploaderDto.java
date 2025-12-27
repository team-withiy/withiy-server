package com.server.domain.route.dto;

import com.server.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UploaderDto {

	private Long userId;
	private String profileUrl;
	private String nickname;

	public static UploaderDto from(User user) {
		if (user == null) {
			return null;
		}
		return UploaderDto.builder()
			.userId(user.getId())
			.profileUrl(user.getThumbnail())
			.nickname(user.getNickname())
			.build();
	}

}
