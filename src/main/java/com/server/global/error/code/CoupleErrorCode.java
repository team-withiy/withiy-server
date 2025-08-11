package com.server.global.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CoupleErrorCode implements ErrorCode {

	COUPLE_ALREADY_CONNECTED(HttpStatus.BAD_REQUEST.value(),
		"Couple is currently connected and not in deleted state."),
	PARTNER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "User with the provided code not found."),
	PARTNER_ALREADY_CONNECTED(HttpStatus.BAD_REQUEST.value(),
		"The partner is already connected as a couple."),
	SELF_CONNECTION_NOT_ALLOWED(HttpStatus.BAD_REQUEST.value(),
		"Cannot connect with oneself as a couple."),
	COUPLE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "Connected couple not found."),
	COUPLE_ALREADY_DISCONNECTED(HttpStatus.BAD_REQUEST.value(),
		"Couple connection has already been disconnected."),
	INVALID_COUPLE_ID(HttpStatus.BAD_REQUEST.value(),
		"Provided couple ID does not match the user's couple.");

	private final int status;
	private final String message;
}
