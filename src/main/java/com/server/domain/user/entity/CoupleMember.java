package com.server.domain.user.entity;

import com.server.global.common.BaseTime;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "couple_member")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoupleMember extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "couple_id", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Couple couple;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	public static CoupleMember of(Couple couple, User user) {
		return CoupleMember.builder()
			.couple(couple)
			.user(user)
			.build();
	}
}

