package com.server.domain.term.entity;

import com.server.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "term_agreement")
@IdClass(TermAgreementId.class)
@EntityListeners(AuditingEntityListener.class)
public class TermAgreement {

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private User user;

	@Id
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "term_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Term term;

	@Column(name = "agreed")
	private boolean agreed;

	@Builder
	public TermAgreement(User user, Term term) {
		this.user = user;
		this.term = term;
		this.agreed = false;
	}

	public void setAgreed(boolean agreed) {
		this.agreed = agreed;
	}
}
