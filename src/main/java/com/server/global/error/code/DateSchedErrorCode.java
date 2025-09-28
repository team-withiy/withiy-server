package com.server.global.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum DateSchedErrorCode implements ErrorCode {
	INVALID_DATE_FORMAT(HttpStatus.BAD_REQUEST.value(), "format은 'month' or 'day'만 가능합니다.");

	private final int status;
	private final String message;

}
