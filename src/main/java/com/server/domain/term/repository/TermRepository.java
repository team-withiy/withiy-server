package com.server.domain.term.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.server.domain.term.entity.Term;

@Repository
public interface TermRepository extends JpaRepository<Term, Long> {
}
