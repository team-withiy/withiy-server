package com.server.global.error.code;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements ErrorCode {
    URL_NOT_PERMITTED(HttpStatus.FORBIDDEN.value(), "URL not permitted."),
    OAUTH_PROCESS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error during OAuth process."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED.value(), "Invalid access token."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED.value(), "Invalid refresh token."),
    ILLEGAL_REGISTRATION_ID(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Invalid registration ID.");

    private final int status;
    private final String message;
}
