package com.server.global.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BadgeErrorCode implements ErrorCode {
	BADGE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "Badge not found."),
	BADGE_ALREADY_EXISTS(HttpStatus.CONFLICT.value(), "Badge already exists."),
	BADGE_NOT_CLAIMED(HttpStatus.BAD_REQUEST.value(), "Badge has not been claimed yet."),
	// 배지 획득 조건이 충족되지 않았을 때 발생하는 에러 코드
	BADGE_CONDITION_NOT_MET(HttpStatus.BAD_REQUEST.value(), "Badge condition not met.");

	private final int status;
	private final String message;
}
