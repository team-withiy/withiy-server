package com.server.domain.course.repository;

import com.server.domain.course.dto.CourseStatus;
import com.server.domain.course.entity.Course;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseRepository extends JpaRepository<Course, Long> {

	List<Course> findByNameContainingIgnoreCase(String keyword);

	@Query("SELECT c FROM Course c " +
		"WHERE c.status = :status " +
		"AND LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) "
	)
	List<Course> findCoursesByStatusAndKeyword(@Param("status") CourseStatus status,
		@Param("keyword") String keyword);

	@Query("SELECT c FROM Course c " +
		"WHERE c.status = :status"
	)
	List<Course> findCoursesByStatus(CourseStatus status);
}
