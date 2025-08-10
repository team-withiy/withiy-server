package com.server.global.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements ErrorCode {
	NOT_FOUND(HttpStatus.UNAUTHORIZED.value(), "User not found."), INVALID_PARAMETER(
		HttpStatus.BAD_REQUEST.value(),
		"Invalid parameter."), ALREADY_ACTIVE(HttpStatus.BAD_REQUEST.value(),
		"Account is already active."), RESTORATION_PERIOD_EXPIRED(
		HttpStatus.GONE.value(),
		"Account restoration period has expired.");

	private final int status;
	private final String message;
}
