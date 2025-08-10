package com.server.domain.course.repository;

import com.server.domain.course.entity.Course;
import com.server.domain.course.entity.CoursePlace;
import com.server.domain.place.entity.Place;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CoursePlaceRepository extends JpaRepository<CoursePlace, Long> {

	@Query("SELECT cp.place FROM CoursePlace cp WHERE cp.course = :course")
	List<Place> findPlacesByCourse(Course course);
}
