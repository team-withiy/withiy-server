package com.server.domain.section.repository;

import com.server.domain.section.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SectionRepository extends JpaRepository<Section, String> {
    Optional<List<Section>> findByIsHome(boolean isHome);
}
