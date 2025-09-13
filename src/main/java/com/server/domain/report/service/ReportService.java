package com.server.domain.report.service;

import com.server.domain.photo.entity.Photo;
import com.server.domain.place.entity.Place;
import com.server.domain.report.dto.ReportReasonType;
import com.server.domain.report.dto.ReportTargetType;
import com.server.domain.report.entity.Report;
import com.server.domain.report.repository.ReportRepository;
import com.server.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService {

	private final ReportRepository reportRepository;

	@Transactional
	public void createReport(User reporter, User reportedUser, Place place, Photo photo,
		ReportTargetType targetType,
		ReportReasonType reasonType, String contents) {
		Report report = Report.builder()
			.reporter(reporter)
			.reportedUser(reportedUser)
			.place(place)
			.photo(photo)
			.targetType(targetType)
			.reasonType(reasonType)
			.contents(contents)
			.build();

		reportRepository.save(report);
	}
}
