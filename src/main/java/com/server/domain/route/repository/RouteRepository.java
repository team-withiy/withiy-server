package com.server.domain.route.repository;

import com.server.domain.route.entity.Route;
import com.server.domain.route.entity.RouteStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RouteRepository extends JpaRepository<Route, Long> {

	List<Route> findByNameContainingIgnoreCase(String keyword);

	@Query("SELECT r FROM Route r " +
		"WHERE r.status = :status " +
		"AND LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%')) "
	)
	List<Route> findRoutesByStatusAndKeyword(@Param("status") RouteStatus status,
		@Param("keyword") String keyword);

	@Query("SELECT r FROM Route r " +
		"WHERE r.status = :status"
	)
	List<Route> findRoutesByStatus(RouteStatus status);

	/**
	 * Route와 RoutePlace를 함께 조회 (N+1 문제 방지)
	 * LEFT JOIN FETCH를 사용하여 RoutePlace 컬렉션을 한 번의 쿼리로 가져옴
	 *
	 * @param id Route ID
	 * @return Route (RoutePlace 포함)
	 */
	@Query("SELECT r FROM Route r " +
		"LEFT JOIN FETCH r.routePlaces " +
		"WHERE r.id = :id")
	Optional<Route> findByIdWithRoutePlaces(@Param("id") Long id);
}
