package com.server.global.error.code;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements ErrorCode {
    NOT_FOUND(HttpStatus.NOT_FOUND.value(), "사용자가 존재하지 않습니다."), INVALID_PARAMETER(
            HttpStatus.BAD_REQUEST.value(), "잘못된 요청입니다."), ALREADY_ACTIVE(
                    HttpStatus.BAD_REQUEST.value(), "이미 활성화된 계정입니다."), RESTORATION_PERIOD_EXPIRED(
                            HttpStatus.GONE.value(), "계정 복구 기간이 만료되었습니다.");

    private final int status;
    private final String message;
}
