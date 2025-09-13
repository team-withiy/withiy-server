package com.server.domain.report.entity;

import com.server.domain.photo.entity.Photo;
import com.server.domain.place.entity.Place;
import com.server.domain.report.dto.ReportReasonType;
import com.server.domain.report.dto.ReportTargetType;
import com.server.domain.user.entity.User;
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
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "report")
public class Report {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "photo_id", nullable = false)
	private Photo photo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "place_id", nullable = false)
	private Place place;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reporter_id", nullable = false)
	private User reporter;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reported_user_id", nullable = false)
	private User reportedUser;

	@Enumerated(EnumType.STRING)
	@Column(name = "target_type", nullable = false)
	private ReportTargetType targetType; // e.g., 신고 대상 타입 (PHOTO, PLACE)

	@Enumerated(EnumType.STRING)
	@Column(name = "reason_type", nullable = false)
	private ReportReasonType reasonType; // e.g., 신고 사유 (PHOTO_INAPPROPRIATE, PLACE_INACCURATE

	@Column(name = "contents", nullable = true, length = 500)
	private String contents;

	@Column(name = "created_at", nullable = false)
	@CreationTimestamp
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	@UpdateTimestamp
	private LocalDateTime updatedAt;

	@Column(name = "deleted_at", nullable = true)
	private LocalDateTime deletedAt;

	@Builder
	public Report(Photo photo, Place place, User reporter, User reportedUser,
		ReportTargetType targetType, ReportReasonType reasonType, String contents) {
		this.photo = photo;
		this.place = place;
		this.reporter = reporter;
		this.reportedUser = reportedUser;
		this.targetType = targetType;
		this.reasonType = reasonType;
		this.contents = contents;
	}

}
