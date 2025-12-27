package com.server.domain.route.service;

import com.server.domain.place.entity.Place;
import com.server.domain.route.entity.Route;
import com.server.domain.route.entity.RouteBookmark;
import com.server.domain.route.entity.RoutePlace;
import com.server.domain.route.entity.RouteStatus;
import com.server.domain.route.repository.RouteBookmarkRepository;
import com.server.domain.route.repository.RoutePlaceRepository;
import com.server.domain.route.repository.RouteRepository;
import com.server.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class RouteService {

	private final RouteRepository routeRepository;
	private final RouteBookmarkRepository routeBookmarkRepository;
	private final RoutePlaceRepository routePlaceRepository;

	public void saveRoute(Route route) {
		routeRepository.save(route);
	}

	public void saveRoutePlace(RoutePlace routePlace) {
		routePlaceRepository.save(routePlace);
	}

	@Transactional(readOnly = true)
	public Optional<Route> findById(Long routeId) {
		return routeRepository.findById(routeId);
	}

	@Transactional(readOnly = true)
	public List<Route> getBookmarkedRoutes(User user) {
		return routeBookmarkRepository.findByUserWithRoute(user).stream()
			.map(RouteBookmark::getRoute)
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<Route> searchByKeyword(String keyword) {
		return routeRepository.findByNameContainingIgnoreCase(keyword);
	}

	public List<Route> getActiveRoutesByKeyword(String keyword) {
		if (keyword == null || keyword.isEmpty()) {
			return routeRepository.findRoutesByStatus(RouteStatus.ACTIVE);
		} else {
			return routeRepository.findRoutesByStatusAndKeyword(RouteStatus.ACTIVE, keyword);
		}
	}

	@Transactional(readOnly = true)
	public long getBookmarkCount(Route route) {
		return routeBookmarkRepository.countByRouteAndNotDeleted(route);
	}

	@Transactional(readOnly = true)
	public List<Place> getPlacesInRoute(Route route) {
		return routePlaceRepository.findPlacesByRoute(route);
	}

	/**
	 * 여러 루트에 속한 모든 장소들을 한 번의 쿼리로 조회
	 *
	 * @param routes 조회할 루트 목록
	 * @return RoutePlace 목록 (루트와 장소 정보 포함)
	 */
	@Transactional(readOnly = true)
	public List<RoutePlace> getPlacesInRoutes(List<Route> routes) {
		if (routes == null || routes.isEmpty()) {
			return List.of();
		}
		return routePlaceRepository.findByRouteIn(routes);
	}
}
