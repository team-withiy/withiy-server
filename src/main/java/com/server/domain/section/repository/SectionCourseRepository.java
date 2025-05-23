package com.server.domain.section.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.server.domain.section.entity.SectionCourse;
import com.server.domain.section.entity.SectionCourseId;

@Repository
public interface SectionCourseRepository extends JpaRepository<SectionCourse, SectionCourseId> {
    Optional<List<SectionCourse>> findAllBySectionId(Long sectionId);

    @Query("SELECT MAX(sp.sequence) FROM SectionCourse sp WHERE sp.section.id = :sectionId")
    Integer findMaxSequenceBySectionId(@Param("sectionId") Long sectionId);
}
