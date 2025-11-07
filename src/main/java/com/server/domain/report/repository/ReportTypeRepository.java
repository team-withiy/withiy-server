package com.server.domain.report.repository;

import com.server.domain.report.entity.ReportReason;
import com.server.domain.report.entity.ReportTarget;
import com.server.domain.report.entity.ReportType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportTypeRepository extends JpaRepository<ReportType, Long> {

	List<ReportType> findByTarget(ReportTarget target);

	Optional<ReportType> findByTargetAndReason(ReportTarget target, ReportReason reason);
}
