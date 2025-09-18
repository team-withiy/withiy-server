package com.server.domain.route.repository;

import com.server.domain.route.dto.RouteStatus;
import com.server.domain.route.entity.Route;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RouteRepository extends JpaRepository<Route, Long> {

	List<Route> findByNameContainingIgnoreCase(String keyword);

	@Query("SELECT c FROM Route c " +
		"WHERE c.status = :status " +
		"AND LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) "
	)
	List<Route> findCoursesByStatusAndKeyword(@Param("status") RouteStatus status,
		@Param("keyword") String keyword);

	@Query("SELECT c FROM Route c " +
		"WHERE c.status = :status"
	)
	List<Route> findCoursesByStatus(RouteStatus status);
}
