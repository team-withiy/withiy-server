package com.server.domain.report.repository;

import com.server.domain.report.entity.Report;
import com.server.domain.report.entity.ReportType;
import com.server.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

	Optional<Report> findReportByTargetIdAndReporterAndReportType(Long targetId, User reporter,
		ReportType type);
}
