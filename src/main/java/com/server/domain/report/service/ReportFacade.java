package com.server.domain.report.service;

import com.server.domain.photo.service.PhotoService;
import com.server.domain.place.service.PlaceService;
import com.server.domain.report.dto.ReportReasonDto;
import com.server.domain.report.dto.ReportTarget;
import com.server.domain.report.dto.request.CreateReportRequest;
import com.server.domain.user.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportFacade {

	private final ReportService reportService;
	private final PlaceService placeService;
	private final PhotoService photoService;

	/**
	 * 신고 생성
	 *
	 * @param reporter 신고자
	 * @param request  신고 요청 정보
	 */
	@Transactional
	public void reportTarget(User reporter, CreateReportRequest request) {

		ReportTarget target = ReportTarget.fromString(request.getTarget());

		switch (target) {
			case PLACE -> placeService.getPlaceById(request.getTargetId());
			case PHOTO -> photoService.getPhotoById(request.getTargetId());
		}

		reportService.createReport(reporter, target, request.getTargetId(),
			request.getReason(), request.getContents());
	}

	@Transactional(readOnly = true)
	public List<ReportReasonDto> getReportReasons(String target) {
		ReportTarget reportTarget = ReportTarget.fromString(target);
		return reportService.getReportReasons(reportTarget);
	}
}
