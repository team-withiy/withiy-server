package com.server.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "couple")
public class Couple {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user1_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private User user1;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user2_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private User user2;

	@Column(name = "first_met_date")
	private LocalDate firstMetDate;

	@Column(name = "created_at", nullable = false)
	@CreationTimestamp
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	@LastModifiedDate
	private LocalDateTime updatedAt;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;


	public Couple(User user1, User user2, LocalDate firstMetDate) {
		this.user1 = user1;
		this.user2 = user2;
		this.firstMetDate = firstMetDate;
	}

	public User getPartnerOf(User user) {
		return user1.getId().equals(user.getId()) ? user2 : user1;
	}

	public boolean isRestorable() {
		return deletedAt != null && Duration.between(deletedAt, LocalDateTime.now()).toDays() < 30;
	}
}
