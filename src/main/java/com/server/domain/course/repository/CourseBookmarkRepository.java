package com.server.domain.course.repository;

import com.server.domain.course.entity.CourseBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CourseBookmarkRepository extends JpaRepository<CourseBookmark, Long> {
    @Query("SELECT COUNT(cb) > 0 FROM CourseBookmark cb WHERE cb.course.id = :courseId AND cb.user.id = :userId")
    boolean existsByCourseIdAndUserId(@Param("courseId") Long courseId, @Param("userId") Long userId);

}
