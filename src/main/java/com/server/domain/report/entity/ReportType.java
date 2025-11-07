package com.server.domain.report.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReportType {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "target_type", nullable = false)
	@Enumerated(EnumType.STRING)
	private ReportTarget target;
	@Column(name = "reason_type", nullable = false)
	@Enumerated(EnumType.STRING)
	private ReportReason reason;
	@Column(name = "description")
	private String description;

}
