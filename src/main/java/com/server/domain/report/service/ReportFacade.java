package com.server.domain.report.service;

import com.server.domain.photo.entity.Photo;
import com.server.domain.photo.service.PhotoService;
import com.server.domain.place.entity.Place;
import com.server.domain.place.service.PlaceService;
import com.server.domain.report.dto.ReportTarget;
import com.server.domain.report.dto.ReportTypeDto;
import com.server.domain.report.dto.request.CreateReportRequest;
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
		if (target.equals(ReportTarget.PLACE)) {
			Place place = placeService.getPlaceById(request.getTargetId());
		} else if (target.equals(ReportTarget.PHOTO)) {
			Photo photo = photoService.getPhotoById(request.getTargetId());
		} else {
			throw new BusinessException(ReportErrorCode.REPORT_TARGET_NOT_FOUND);
		}

		reportService.createReport(reporter, target, request.getTargetId(),
			request.getReason(), request.getContents());
	}

	@Transactional(readOnly = true)
	public List<ReportTypeDto> getReportTypes(String target) {
		ReportTarget reportTarget = ReportTarget.fromString(target);
		return reportService.getReportTypes(reportTarget);
	}
}
