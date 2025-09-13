package com.server.domain.report.controller;

import com.server.domain.report.dto.request.CreateReportRequest;
import com.server.domain.report.service.ReportFacade;
import com.server.domain.user.entity.User;
import com.server.global.dto.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/reports")
public class ReportController {

	private final ReportFacade reportFacade;

	@PreAuthorize("hasRole('USER')")
	@ResponseStatus(HttpStatus.OK)
	@PostMapping
	@Operation(summary = "신고 생성 API", description = "사용자가 사진, 장소 등을 신고하는 API")
	public ApiResponseDto<String> createReport(@AuthenticationPrincipal User user,
		@RequestBody CreateReportRequest request) {
		reportFacade.createReport(user, request);
		return ApiResponseDto.success(HttpStatus.OK.value(), "신고가 성공적으로 접수되었습니다.");
	}

}
