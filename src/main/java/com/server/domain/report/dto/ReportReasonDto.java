package com.server.domain.report.dto;

import com.server.domain.report.entity.ReportReason;
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
public class ReportReasonDto {

	private ReportReason reason;
	private String description;

	public static ReportReasonDto from(ReportType type) {
		return ReportReasonDto.builder()
			.reason(type.getReason())
			.description(type.getDescription())
			.build();
	}
}
