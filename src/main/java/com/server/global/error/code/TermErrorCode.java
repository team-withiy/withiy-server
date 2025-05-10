package com.server.global.error.code;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TermErrorCode implements ErrorCode {
    REQUIRED_TERM_NOT_AGREED(HttpStatus.BAD_REQUEST.value(), "필수적 동의 항목에 동의하지 않았습니다.");

    private final int status;
    private final String message;
}
