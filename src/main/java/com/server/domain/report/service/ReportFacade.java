package com.server.domain.report.service;

import com.server.domain.photo.entity.Photo;
import com.server.domain.photo.service.PhotoService;
import com.server.domain.place.entity.Place;
import com.server.domain.place.service.PlaceService;
import com.server.domain.report.dto.ReportReasonType;
import com.server.domain.report.dto.ReportTargetType;
import com.server.domain.report.dto.request.CreateReportRequest;
import com.server.domain.user.entity.User;
import com.server.domain.user.service.UserService;
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
	private final UserService userService;

	/**
	 * 신고 생성
	 *
	 * @param reporter 신고자
	 * @param request  신고 요청 정보
	 */
	@Transactional
	public void createReport(User reporter, CreateReportRequest request) {

		Place place = placeService.getPlaceById(request.getPlaceId());
		Photo photo = photoService.getPhotoById(request.getPhotoId());

		// TODO: place의 photo인지 검증 필요.
		//  photo, place, dateSchedule 엔티티 관계 재설계 후 검증 로직 추가

		User reportedUser = userService.getUserWithPersonalInfo(request.getReportedUserId());
		String contents = request.getContents();
		ReportTargetType reportTargetType = request.getTargetType();
		ReportReasonType reportReasonType = request.getReasonType();
		reportService.createReport(reporter, reportedUser, place, photo, reportTargetType,
			reportReasonType, contents);
	}

}
