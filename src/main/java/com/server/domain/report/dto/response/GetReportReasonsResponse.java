package com.server.domain.report.dto.response;

import com.server.domain.report.dto.ReportReasonType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "신고 사유 목록 응답",
	example = "{\n" +
		"  \"reasons\": [\n" +
		"    \"PHOTO_INAPPROPRIATE\",\n" +
		"    \"PHOTO_COPYRIGHT\",\n" +
		"    \"PHOTO_SPAM\",\n" +
		"    \"PLACE_INACCURATE\",\n" +
		"    \"PLACE_DUPLICATE\",\n" +
		"    \"PLACE_INAPPROPRIATE\",\n" +
		"    \"OTHER\"\n" +
		"  ]\n" +
		"}")
public class GetReportReasonsResponse {

	List<ReportReasonType> reasons;

	public static GetReportReasonsResponse of(List<ReportReasonType> reasons) {
		return new GetReportReasonsResponse(reasons);
	}

}
