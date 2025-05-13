package com.server.global.error.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MapErrorCode implements ErrorCode {

    API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Error calling Map API."), INVALID_PARAMETER(
            HttpStatus.BAD_REQUEST, "Invalid parameter."), INVALID_COORDINATES(HttpStatus.BAD_REQUEST,
                    "Invalid coordinates."), INVALID_ADDRESS(HttpStatus.BAD_REQUEST, "Invalid address.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public int getStatus() {
        return httpStatus.value();
    }
}
