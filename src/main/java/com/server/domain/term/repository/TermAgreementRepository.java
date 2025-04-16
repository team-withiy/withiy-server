package com.server.domain.term.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.server.domain.term.entity.Term;
import com.server.domain.term.entity.TermAgreement;
import com.server.domain.term.entity.TermAgreementId;

@Repository
public interface TermAgreementRepository extends JpaRepository<TermAgreement, TermAgreementId> {
    Optional<List<Term>> findAllByUserId(Long userId);
}
