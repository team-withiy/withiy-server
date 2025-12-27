package com.server.domain.search.service;

import com.server.domain.map.dto.MapPlaceDto;
import com.server.domain.map.dto.request.KeywordSearchRequest;
import com.server.domain.map.service.MapService;
import com.server.domain.photo.dto.PhotoSummary;
import com.server.domain.photo.service.PhotoService;
import com.server.domain.place.dto.PlaceDto;
import com.server.domain.place.service.PlaceService;
import com.server.domain.route.dto.RouteDto;
import com.server.domain.route.dto.RoutePlaceSummary;
import com.server.domain.route.entity.Route;
import com.server.domain.route.entity.RoutePlace;
import com.server.domain.route.service.RouteService;
import com.server.domain.search.dto.SearchHistoryDto;
import com.server.domain.search.dto.SearchResultResponse;
import com.server.domain.search.dto.request.SearchResultRequest;
import com.server.domain.search.entity.SearchPageType;
import com.server.domain.search.entity.SearchTargetType;
import com.server.domain.user.entity.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SearchFacade {

	private final SearchService searchService;
	private final PlaceService placeService;
	private final RouteService routeService;
	private final PhotoService photoService;
	private final MapService mapService;

	/**
	 * 최근 검색어 조회
	 *
	 * @param user
	 * @return 최근 검색어 목록
	 */
	@Transactional(readOnly = true)
	public List<SearchHistoryDto> getRecentSearches(User user) {
		return searchService.getRecentSearchHistory(user);
	}

	/**
	 * 키워드, 필터, 정렬 기준에 따른 장소 및 루트 검색
	 *
	 * @param user             사용자 정보
	 * @param searchRequestDto 검색 요청 정보
	 * @return 검색 결과 (장소 및 루트 목록)
	 */
	@Transactional
	public SearchResultResponse search(User user, SearchResultRequest searchRequestDto) {

		String keyword = searchRequestDto.getKeyword();
		SearchPageType pageType = searchRequestDto.getPageType();
		SearchTargetType targetType = searchRequestDto.getTargetType();
		SearchResultResponse searchResultResponse = new SearchResultResponse();

		// 1. 검색어 저장
		searchService.saveSearchHistory(user, keyword);

		// 2. 검색 대상 타입에 따라 분기 처리
		if (targetType == SearchTargetType.PLACE) {

			// 장소 검색 - 내부 DB 조회
			List<PlaceDto> searchPlaces = searchPlaces(keyword, pageType);
			searchResultResponse.setSearchPlaces(searchPlaces);
		} else if (targetType == SearchTargetType.ROUTE) {
			// 루트 검색
			List<RouteDto> searchRoutes = searchRoutes(keyword, user);
			searchResultResponse.setSearchRoutes(searchRoutes);
		}
		return searchResultResponse;
	}

	/**
	 * 키워드로 장소 검색
	 *
	 * @param keyword  검색 키워드
	 * @param pageType 검색 페이지 타입
	 * @return 검색된 장소 목록
	 */
	@Transactional(readOnly = true)
	public List<PlaceDto> searchPlaces(String keyword, SearchPageType pageType) {
		List<PlaceDto> searchPlaces = new ArrayList<>();

		// 장소 검색 - 내부 DB 조회
		searchPlaces = placeService.searchByKeyword(keyword);

		// 일정 생성 페이지에서는 카카오 API 결과도 포함
		if (pageType == SearchPageType.DATE_SCHEDULE) {
			List<MapPlaceDto> searchMapPlaces = mapService.searchByKeyword(
				KeywordSearchRequest.builder()
					.query(keyword)
					.build());

			// MapPlaceDto -> PlaceDto 변환 후 내부 DB 결과에 추가
			List<PlaceDto> places = searchMapPlaces.stream()
				.map(MapPlaceDto::toPlaceDto)
				.collect(Collectors.toList());

			searchPlaces.addAll(places);
		}
		return searchPlaces;
	}


	/**
	 * 키워드로 루트 검색
	 *
	 * @param keyword 검색 키워드
	 * @param user    사용자 정보 (북마크 여부 확인용)
	 * @return 검색된 루트 목록
	 */
	@Transactional(readOnly = true)
	public List<RouteDto> searchRoutes(String keyword, User user) {
		// 1. Route 검색
		List<Route> routes = routeService.searchByKeyword(keyword);

		if (routes.isEmpty()) {
			return List.of();
		}

		// 2. 모든 관련 RoutePlace를 한 번의 쿼리로 가져와 Route ID로 그룹화
		Map<Long, List<RoutePlace>> routePlacesByRouteId =
			routeService.getPlacesInRoutes(routes).stream()
				.collect(Collectors.groupingBy(rp -> rp.getRoute().getId()));

		// 3. 모든 Route에 포함된 모든 Place의 ID를 수집
		List<Long> allPlaceIds = routePlacesByRouteId.values().stream()
			.flatMap(List::stream)
			.map(rp -> rp.getPlace().getId())
			.distinct()
			.collect(Collectors.toList());

		// 4. 모든 Place의 대표 사진을 한 번의 쿼리로 가져옴 (N+1 문제 해결)
		Map<Long, PhotoSummary> photoSummaryByPlaceId = new HashMap<>();
		if (!allPlaceIds.isEmpty()) {
			Map<Long, List<PhotoSummary>> photoSummariesMap =
				photoService.getPlacePhotoSummariesMap(allPlaceIds);
			// 각 장소의 첫 번째 사진을 대표 사진으로 사용
			photoSummariesMap.forEach((placeId, summaries) -> {
				if (!summaries.isEmpty()) {
					photoSummaryByPlaceId.put(placeId, summaries.get(0));
				}
			});
		}

		// 5. 사용자가 북마크한 Route ID를 Set으로 가져와 O(1) 조회 지원
		final Set<Long> bookmarkedRouteIdSet;
		if (user != null) {
			bookmarkedRouteIdSet = routeService.getBookmarkedRoutes(user).stream()
				.map(Route::getId)
				.collect(Collectors.toSet());
		} else {
			bookmarkedRouteIdSet = new HashSet<>();
		}

		final Map<Long, PhotoSummary> finalPhotoSummaryMap = photoSummaryByPlaceId;

		return routes.stream()
			.map(route -> {
				List<RoutePlace> currentRoutePlaces =
					routePlacesByRouteId.getOrDefault(route.getId(), List.of());

				// 해당 루트의 장소 목록 추출
				List<RoutePlaceSummary> places = currentRoutePlaces.stream()
					.map(RoutePlaceSummary::from)
					.collect(Collectors.toList());

				// 이미지 목록 생성 (장소별 대표 이미지, 최대 10장) - Map을 사용하여 N+1 문제 해결
				List<PhotoSummary> images = currentRoutePlaces.stream()
					.limit(10)
					.map(rp -> finalPhotoSummaryMap.get(rp.getPlace().getId()))
					.filter(java.util.Objects::nonNull)
					.collect(Collectors.toList());

				// 북마크 여부 확인 (Set 사용으로 O(1) 조회)
				boolean bookmarked = bookmarkedRouteIdSet.contains(route.getId());

				return RouteDto.builder()
					.id(route.getId())
					.name(route.getName())
					.places(places)
					.photos(images)
					.bookmarked(bookmarked)
					.build();
			})
			.collect(Collectors.toList());
	}
}
