package com.server.domain.report.dto;

import com.server.domain.report.entity.ReportType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "신고 유형 DTO")
public class ReportTypeDto {

	private ReportReason reason;
	private String description;

	public static ReportTypeDto from(ReportType type) {
		return ReportTypeDto.builder()
			.reason(type.getReason())
			.description(type.getDescription())
			.build();
	}
}
