package com.server.global.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ProjectErrorCode implements ErrorCode {
	NOT_FOUND(HttpStatus.NOT_FOUND.value(), "Project not found.");

	private final int status;
	private final String message;
}
