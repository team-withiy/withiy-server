package com.server.domain.term.dto;

import com.server.domain.term.entity.TermAgreement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TermAgreementDto {

	private TermDto term;
	private boolean isAgreed;

	public static TermAgreementDto from(TermAgreement termAgreement) {
		return TermAgreementDto.builder()
			.term(TermDto.from(termAgreement.getTerm()))
			.isAgreed(termAgreement.isAgreed())
			.build();
	}
}
