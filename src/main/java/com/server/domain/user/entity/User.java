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
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Slf4j
public class User extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "is_admin", nullable = false)
	private boolean isAdmin;

	@Column(name = "refresh_token", length = 512)
	private String refreshToken;

	@Column(name = "nickname", nullable = true)
	private String nickname;

	@Column(name = "thumbnail")
	private String thumbnail;

	@Column(name = "code", nullable = true)
	private String code;

	@Column(name = "deleted_at", nullable = true)
	private LocalDateTime deletedAt;

	// date_notification_enabled
	@Column(name = "date_notification_enabled", nullable = false)
	private Boolean dateNotificationEnabled = true;
	// event_notification_enabled
	@Column(name = "event_notification_enabled", nullable = false)
	private Boolean eventNotificationEnabled = true;


	@Builder
	public User(String nickname, String thumbnail, String code) {
		this.nickname = nickname;
		this.thumbnail = thumbnail;
		this.isAdmin = false;
		this.code = code;
	}

	public void updateRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public boolean isRestorable() {
		return deletedAt != null && Duration.between(deletedAt, LocalDateTime.now()).toDays() < 30;
	}

	public void updateDeletedAt(LocalDateTime deletedAt) {
		this.deletedAt = deletedAt;
	}

	public void updateNickname(String nickname) {
		this.nickname = nickname;
	}

	public void updateThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public void updateDateNotificationEnabled(Boolean enabled) {
		this.dateNotificationEnabled = enabled;
	}

	public void updateEventNotificationEnabled(Boolean enabled) {
		this.eventNotificationEnabled = enabled;
	}

}
