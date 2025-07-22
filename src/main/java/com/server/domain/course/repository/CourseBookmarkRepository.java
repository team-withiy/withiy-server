package com.server.domain.course.repository;

import com.server.domain.course.entity.CourseBookmark;
import com.server.domain.user.entity.User;
import io.micrometer.common.KeyValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseBookmarkRepository extends JpaRepository<CourseBookmark, Long> {
    @Query("SELECT COUNT(cb) > 0 FROM CourseBookmark cb WHERE cb.course.id = :courseId AND cb.user.id = :userId")
    boolean existsByCourseIdAndUserId(@Param("courseId") Long courseId, @Param("userId") Long userId);

    @Query("SELECT cb FROM CourseBookmark cb JOIN FETCH cb.course WHERE cb.user = :user")
    List<CourseBookmark> findByUserWithCourse(User user);
}
