package com.server.global.error.code;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReviewErrorCode implements ErrorCode {
	REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 리뷰를 찾을 수 없습니다.");

	private final int status;
	private final String message;

}
