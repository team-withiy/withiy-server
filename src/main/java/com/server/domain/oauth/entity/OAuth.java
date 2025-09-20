package com.server.domain.oauth.entity;

import com.server.domain.user.entity.User;
import com.server.global.common.BaseTime;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "oauth")
@EntityListeners(AuditingEntityListener.class)
public class OAuth extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "provider")
	private String provider;

	@Column(name = "provider_id")
	private String providerId;

	@Column(name = "email")
	private String email;

	@Column(name = "nickname")
	private String nickname;

	@Column(name = "thumbnail")
	private String thumbnail;

	// NOTE: OAuth 저장 시 User도 함께 저장되도록
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Builder
	public OAuth(String provider, String providerId, String email, User user, String nickname,
		String thumbnail) {
		this.provider = provider;
		this.providerId = providerId;
		this.email = email;
		this.user = user;
		this.nickname = nickname;
		this.thumbnail = thumbnail;
	}

	public void updateThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
}
