package com.server.domain.route.repository;

import com.server.domain.place.entity.Place;
import com.server.domain.route.entity.Route;
import com.server.domain.route.entity.RoutePlace;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoutePlaceRepository extends JpaRepository<RoutePlace, Long> {

	@Query("SELECT cp.place FROM RoutePlace cp WHERE cp.route = :route")
	List<Place> findPlacesByRoute(Route route);

	@Query("SELECT rp FROM RoutePlace rp JOIN FETCH rp.place WHERE rp.route IN :routes ORDER BY rp.route.id, rp.id")
	List<RoutePlace> findByRouteIn(List<Route> routes);
}
