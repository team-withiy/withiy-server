package com.server.global.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TermErrorCode implements ErrorCode {
	REQUIRED_TERM_NOT_AGREED(HttpStatus.BAD_REQUEST.value(), "Required term not agreed."),
	TERM_AGREEMENT_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "Term agreement not found.");

	private final int status;
	private final String message;
}
