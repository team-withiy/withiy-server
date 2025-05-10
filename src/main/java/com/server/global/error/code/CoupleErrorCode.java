package com.server.global.error.code;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CoupleErrorCode implements ErrorCode {

    ALREADY_CONNECTED(HttpStatus.BAD_REQUEST.value(), "이미 커플로 연결된 사용자입니다."), PARTNER_NOT_FOUND(
            HttpStatus.NOT_FOUND.value(),
            "해당 코드를 가진 사용자를 찾을 수 없습니다."), PARTNER_ALREADY_CONNECTED(HttpStatus.BAD_REQUEST.value(),
                    "상대방이 이미 커플로 연결된 사용자입니다."), SELF_CONNECTION_NOT_ALLOWED(
                            HttpStatus.BAD_REQUEST.value(),
                            "자기 자신과 커플 연결을 할 수 없습니다."), COUPLE_NOT_FOUND(
                                    HttpStatus.NOT_FOUND.value(), "연결된 커플을 찾을 수 없습니다.");

    private final int status;
    private final String message;
}
