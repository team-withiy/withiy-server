package com.server.domain.report.dto;

import lombok.Getter;

@Getter
public enum ReportTargetType {
	PHOTO("사진"),
	PLACE("장소");

	private final String description;

	ReportTargetType(String description) {
		this.description = description;
	}

}
