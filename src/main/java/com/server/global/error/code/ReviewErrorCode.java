package com.server.global.error.code;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReviewErrorCode implements ErrorCode {
	REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 리뷰를 찾을 수 없습니다."),
	INVALID_REVIEW_SORT_TYPE(HttpStatus.BAD_REQUEST.value(), "유효하지 않은 리뷰 정렬 타입입니다.");

	private final int status;
	private final String message;

}
