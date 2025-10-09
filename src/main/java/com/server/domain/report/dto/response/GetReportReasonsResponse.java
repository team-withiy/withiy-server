package com.server.domain.report.dto.response;

import com.server.domain.report.dto.ReportReasonDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetReportReasonsResponse {

	List<ReportReasonDto> types;

	public static GetReportReasonsResponse of(List<ReportReasonDto> types) {
		return new GetReportReasonsResponse(types);
	}
}
