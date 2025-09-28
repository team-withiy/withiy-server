package com.server.domain.route.repository;

import com.server.domain.route.entity.Route;
import com.server.domain.route.entity.RouteBookmark;
import com.server.domain.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RouteBookmarkRepository extends JpaRepository<RouteBookmark, Long> {

	@Query("SELECT COUNT(cb) > 0 FROM RouteBookmark cb WHERE cb.route.id = :route AND cb.user.id = :userId")
	boolean existsByRouteIdAndUserId(@Param("route") Long route,
		@Param("userId") Long userId);

	@Query("SELECT cb FROM RouteBookmark cb JOIN cb.route WHERE cb.user = :user")
	List<RouteBookmark> findByUserWithCourse(User user);

	@Query("SELECT COUNT(cb) FROM RouteBookmark cb " +
		"WHERE cb.route = :route AND cb.deletedAt IS NULL")
	long countByRouteAndNotDeleted(Route route);
}
