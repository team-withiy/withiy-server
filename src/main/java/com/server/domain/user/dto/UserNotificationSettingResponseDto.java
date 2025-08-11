package com.server.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserNotificationSettingResponseDto {

	private Long userId;
	private Boolean dateNotificationEnabled;
	private Boolean eventNotificationEnabled;

	public static UserNotificationSettingResponseDto of(Long userId,
		Boolean dateNotificationEnabled, Boolean eventNotificationEnabled) {
		return UserNotificationSettingResponseDto.builder()
			.dateNotificationEnabled(dateNotificationEnabled)
			.eventNotificationEnabled(eventNotificationEnabled)
			.userId(userId)
			.build();
	}
}
