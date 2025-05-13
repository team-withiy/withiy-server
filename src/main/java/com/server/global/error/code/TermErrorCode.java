package com.server.global.error.code;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TermErrorCode implements ErrorCode {
    REQUIRED_TERM_NOT_AGREED(HttpStatus.BAD_REQUEST.value(), "Required term not agreed.");

    private final int status;
    private final String message;
}
