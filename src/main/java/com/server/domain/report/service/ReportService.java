package com.server.domain.report.service;

import com.server.domain.report.dto.ReportReason;
import com.server.domain.report.dto.ReportStatus;
import com.server.domain.report.dto.ReportTarget;
import com.server.domain.report.dto.ReportTypeDto;
import com.server.domain.report.entity.Report;
import com.server.domain.report.entity.ReportType;
import com.server.domain.report.repository.ReportRepository;
import com.server.domain.report.repository.ReportTypeRepository;
import com.server.domain.user.entity.User;
import com.server.global.error.code.ReportErrorCode;
import com.server.global.error.exception.BusinessException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService {

	private final ReportRepository reportRepository;
	private final ReportTypeRepository reportTypeRepository;

	@Transactional
	public void createReport(User reporter, ReportTarget target, Long targetId,
		ReportReason reason, String contents) {

		ReportType type = reportTypeRepository.findByTargetAndReason(target, reason)
			.orElseThrow(() -> new BusinessException(ReportErrorCode.REPORT_REASON_NOT_FOUND));

		// 중복 신고 확인
		reportRepository.findReportByTargetIdAndReporterAndReportType(targetId, reporter, type)
			.ifPresent(report -> {
				throw new BusinessException(ReportErrorCode.DUPLICATE_REPORT);
			});

		Report report = Report.builder()
			.reporter(reporter)
			.targetId(targetId)
			.reportType(type)
			.contents(contents)
			.status((ReportStatus.PENDING))
			.build();

		reportRepository.save(report);
	}

	@Transactional(readOnly = true)
	public List<ReportTypeDto> getReportTypes(ReportTarget target) {
		if (target.equals(ReportTarget.PHOTO) || target.equals(
			ReportTarget.PLACE)) {
			return reportTypeRepository.findByTarget(target).stream()
				.map(ReportTypeDto::from)
				.toList();

		} else {
			throw new BusinessException(ReportErrorCode.REPORT_TARGET_NOT_FOUND);
		}
	}
}
