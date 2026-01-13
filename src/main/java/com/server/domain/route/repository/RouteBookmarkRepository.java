package com.server.domain.route.repository;

import com.server.domain.route.entity.Route;
import com.server.domain.route.entity.RouteBookmark;
import com.server.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RouteBookmarkRepository extends JpaRepository<RouteBookmark, Long> {

	@Query("SELECT cb FROM RouteBookmark cb JOIN FETCH cb.route WHERE cb.user = :user AND cb.deletedAt IS NULL")
	List<RouteBookmark> findByUserWithRoute(User user);

	@Query("SELECT COUNT(cb) FROM RouteBookmark cb " +
		"WHERE cb.route = :route AND cb.deletedAt IS NULL")
	long countByRouteAndNotDeleted(Route route);

	Optional<RouteBookmark> findByRouteAndUser(Route route, User user);

	boolean existsByRouteAndUserAndDeletedAtIsNull(Route route, User user);
}
