package com.server.domain.report.entity;

import com.server.domain.report.dto.ReportStatus;
import com.server.domain.user.entity.User;
import com.server.global.common.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Table(name = "report")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Report extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reporter_id", nullable = false)
	private User reporter;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "report_type_id", nullable = false)
	private ReportType reportType;

	@Column(name = "target_id", nullable = false)
	private Long targetId; // 신고 대상의 PK

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ReportStatus status; // PENDING, APPROVED, REJECTED

	@Column(length = 500)
	private String contents;

}
