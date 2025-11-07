package com.server.domain.report.entity;

import com.server.global.error.code.ReportErrorCode;
import com.server.global.error.exception.BusinessException;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum ReportTarget {
	PHOTO("사진"),
	PLACE("장소");

	private final String description;

	ReportTarget(String description) {
		this.description = description;
	}

	public static ReportTarget fromString(String value) {
		return Arrays.stream(values())
			.filter(v -> v.name().equalsIgnoreCase(value))
			.findFirst()
			.orElseThrow(() -> new BusinessException(ReportErrorCode.REPORT_TARGET_NOT_FOUND));
	}

}
