package com.server.global.error.code;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CoupleErrorCode implements ErrorCode {

    COUPLE_ALREADY_CONNECTED(HttpStatus.BAD_REQUEST.value(), "User is already connected as a couple."),
    PARTNER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "User with the provided code not found."),
    PARTNER_ALREADY_CONNECTED(HttpStatus.BAD_REQUEST.value(), "The partner is already connected as a couple."),
    SELF_CONNECTION_NOT_ALLOWED(HttpStatus.BAD_REQUEST.value(), "Cannot connect with oneself as a couple."),
    COUPLE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "Connected couple not found."),
    COUPLE_ALREADY_DISCONNECTED(HttpStatus.BAD_REQUEST.value(), "Couple connection has already been disconnected.");

    private final int status;
    private final String message;
}
