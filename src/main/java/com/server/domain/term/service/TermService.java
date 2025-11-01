package com.server.domain.term.service;

import com.server.domain.term.dto.TermDto;
import com.server.domain.term.entity.TermAgreement;
import com.server.domain.term.repository.TermAgreementRepository;
import com.server.domain.term.repository.TermRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TermService {

	private final TermRepository termRepository;
	private final TermAgreementRepository termAgreementRepository;

	@Transactional(readOnly = true)
	public List<TermDto> getAllTerms() {
		return termRepository.findAll().stream().map(
			term -> TermDto.from(term)).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<TermAgreement> getUserTermAgreements(Long userId) {
		return termAgreementRepository.findAllByUserId(userId);
	}

	@Transactional
	public void saveAllTermAgreements(List<TermAgreement> agreements) {
		termAgreementRepository.saveAll(agreements);
	}
}
