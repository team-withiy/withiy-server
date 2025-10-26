package com.server.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.server.domain.oauth.entity.OAuth;
import com.server.domain.term.entity.Term;
import com.server.domain.term.entity.TermAgreement;
import com.server.global.common.BaseTime;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<CoupleMember> coupleMembers = new ArrayList<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true,
		fetch = FetchType.LAZY)
	@JsonIgnore
	private List<OAuth> oAuth = new ArrayList<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true,
		fetch = FetchType.LAZY)
	@JsonIgnore
	private List<TermAgreement> termAgreements = new ArrayList<>();


	// date_notification_enabled
	@Column(name = "date_notification_enabled", nullable = false)
	private Boolean dateNotificationEnabled = true;
	// event_notification_enabled
	@Column(name = "event_notification_enabled", nullable = false)
	private Boolean eventNotificationEnabled = true;


	@Builder
	public User(String nickname, String thumbnail, List<Term> terms, String code) {
		this.nickname = nickname;
		this.thumbnail = thumbnail;
		this.isAdmin = false;
		this.code = code;
		for (Term term : terms) {
			this.termAgreements.add(TermAgreement.builder().user(this).term(term).build());
		}
	}

	public void updateRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public boolean hasAgreedToAllRequiredTerms() {
		if (termAgreements == null || termAgreements.isEmpty()) {
			return false;
		}

		for (TermAgreement agreement : termAgreements) {
			if (agreement.getTerm().isRequired() && !agreement.isAgreed()) {
				return false;
			}
		}
		return true;
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

	public void updateTermAgreements(List<TermAgreement> termAgreements) {
		this.termAgreements = termAgreements;
	}
}
