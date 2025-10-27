package com.server.domain.term.repository;

import com.server.domain.term.entity.TermAgreement;
import com.server.domain.term.entity.TermAgreementId;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TermAgreementRepository extends JpaRepository<TermAgreement, TermAgreementId> {
	
	@EntityGraph(attributePaths = "term")
	List<TermAgreement> findByUserId(@Param("userId") Long userId);
}
