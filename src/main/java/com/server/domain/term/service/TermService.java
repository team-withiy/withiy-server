package com.server.domain.term.service;

import com.server.domain.term.dto.TermDto;
import com.server.domain.term.repository.TermAgreementRepository;
import com.server.domain.term.repository.TermRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TermService {

	private final TermRepository termRepository;
	private final TermAgreementRepository termAgreementRepository;

	public List<TermDto> getAllTerms() {
		return termRepository.findAll().stream().map(
			term -> TermDto.from(term)).collect(Collectors.toList());
	}
}
