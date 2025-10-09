package com.server.domain.report.controller;

import com.server.domain.report.dto.ReportTypeDto;
import com.server.domain.report.dto.request.CreateReportRequest;
import com.server.domain.report.dto.response.GetReportTypesResponse;
import com.server.domain.report.service.ReportFacade;
import com.server.domain.user.entity.User;
import com.server.global.dto.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/reports")
public class ReportController {

	private final ReportFacade reportFacade;

	@ResponseStatus(HttpStatus.OK)
	@GetMapping
	@Operation(summary = "신고 사유 조회 API", description = "사용자가 신고 사유 목록을 조회하는 API")
	public ApiResponseDto<GetReportTypesResponse> getReportReasons(
		@RequestParam String target) {
		List<ReportTypeDto> types = reportFacade.getReportTypes(target);
		return ApiResponseDto.success(HttpStatus.OK.value(),
			GetReportTypesResponse.of(types));
	}

	@PreAuthorize("hasRole('USER')")
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping
	@Operation(summary = "신고 생성 API", description = "사용자가 사진, 장소 등을 신고하는 API")
	public ApiResponseDto<String> createReport(@AuthenticationPrincipal User user,
		@RequestBody CreateReportRequest request) {
		reportFacade.reportTarget(user, request);
		return ApiResponseDto.success(HttpStatus.CREATED.value(), "신고가 성공적으로 접수되었습니다.");
	}

}
