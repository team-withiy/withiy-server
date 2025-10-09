package com.server.global.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReportErrorCode implements ErrorCode {
	REPORT_TARGET_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "신고 대상이 존재하지 않습니다."),
	REPORT_REASON_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "신고 사유가 존재하지 않습니다."),
	DUPLICATE_REPORT(HttpStatus.BAD_REQUEST.value(), "이미 신고한 대상입니다.");

	private final int status;
	private final String message;
}
