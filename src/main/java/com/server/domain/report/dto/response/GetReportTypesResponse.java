package com.server.domain.report.dto.response;

import com.server.domain.report.dto.ReportTypeDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetReportTypesResponse {

	List<ReportTypeDto> types;

	public static GetReportTypesResponse of(List<ReportTypeDto> types) {
		return new GetReportTypesResponse(types);
	}
}
