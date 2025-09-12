package com.server.global.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PhotoErrorCode implements ErrorCode {
	PHOTO_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 사진이 존재하지 않습니다."),
	PHOTO_ALREADY_EXISTS(HttpStatus.BAD_REQUEST.value(), "이미 존재하는 사진입니다.");


	private final int status;
	private final String message;

}
