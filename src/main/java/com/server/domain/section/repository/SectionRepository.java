package com.server.domain.section.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.server.domain.section.entity.Section;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
    Optional<List<Section>> findByIsHome(boolean isHome);
}
