package com.server.domain.user.entity;

import com.server.global.common.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "couple")
public class Couple extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "first_met_date")
	private LocalDate firstMetDate;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	public Couple(LocalDate firstMetDate) {
		this.firstMetDate = firstMetDate;
	}

	public boolean isRestorable() {
		return deletedAt != null && Duration.between(deletedAt, LocalDateTime.now()).toDays() < 30;
	}

	public void updateDeletedAt(LocalDateTime deletedAt) {
		this.deletedAt = deletedAt;
	}

	public void updateFirstMetDate(LocalDate firstMetDate) {
		this.firstMetDate = firstMetDate;
	}
}
