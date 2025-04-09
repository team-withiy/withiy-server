package com.server.domain.section.repository;

import com.server.domain.section.entity.SectionCourse;
import com.server.domain.section.entity.SectionCourseId;
import com.server.domain.section.entity.SectionPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SectionCourseRepository extends JpaRepository<SectionCourse, SectionCourseId> {
    Optional<List<SectionCourse>> findAllBySectionId(Long sectionId);
}
