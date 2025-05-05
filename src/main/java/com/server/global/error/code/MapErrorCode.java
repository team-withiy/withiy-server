package com.server.global.error.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MapErrorCode implements ErrorCode {

    API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Map API 호출 중 오류가 발생했습니다."), INVALID_PARAMETER(
            HttpStatus.BAD_REQUEST, "유효하지 않은 매개변수입니다."), INVALID_COORDINATES(HttpStatus.BAD_REQUEST,
                    "유효하지 않은 좌표입니다."), INVALID_ADDRESS(HttpStatus.BAD_REQUEST, "유효하지 않은 주소입니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public int getStatus() {
        return httpStatus.value();
    }
}
