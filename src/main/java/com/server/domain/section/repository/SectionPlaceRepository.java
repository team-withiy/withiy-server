package com.server.domain.section.repository;

import com.server.domain.section.entity.SectionPlace;
import com.server.domain.section.entity.SectionPlaceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface SectionPlaceRepository extends JpaRepository<SectionPlace, SectionPlaceId> {
    Optional<List<SectionPlace>> findAllBySectionId(Long sectionId);
}
