package com.server.domain.report.dto.request;

import com.server.domain.report.entity.ReportReason;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "신고 생성 요청 DTO")
public class CreateReportRequest {

	@Schema(description = "신고 대상 타입", example = "PHOTO", allowableValues = {"PHOTO", "PLACE"})
	private String target;
	@Schema(description = "신고 대상 ID", example = "123")
	private Long targetId;
	@Schema(description = "신고 사유 타입", example = "PHOTO_INAPPROPRIATE", allowableValues = {
		"PHOTO_INAPPROPRIATE", "PHOTO_COPYRIGHT", "PHOTO_SPAM",
		"PLACE_INACCURATE", "PLACE_DUPLICATE", "PLACE_INAPPROPRIATE",
		"OTHER"})
	private ReportReason reason;
	@Schema(description = "신고 내용 (기타 사유일 경우 필수)", example = "이 사진은 부적절합니다.")
	private String contents;
}
