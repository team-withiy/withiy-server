package com.server.domain.search.service;

import com.server.domain.map.dto.MapPlaceDto;
import com.server.domain.map.dto.request.KeywordSearchRequest;
import com.server.domain.map.service.MapService;
import com.server.domain.place.dto.PlaceDto;
import com.server.domain.place.service.PlaceService;
import com.server.domain.route.dto.response.RouteSearchResponse;
import com.server.domain.route.service.RouteService;
import com.server.domain.search.dto.SearchHistoryDto;
import com.server.domain.search.dto.SearchResultResponse;
import com.server.domain.search.dto.request.SearchResultRequest;
import com.server.domain.search.entity.SearchPageType;
import com.server.domain.search.entity.SearchTargetType;
import com.server.domain.user.entity.User;
import java.util.ArrayList;
import java.util.List;
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
	private final MapService mapService;

	/**
	 * 키워드, 필터, 정렬 기준에 따른 장소 및 코스 검색
	 *
	 * @param user             사용자 정보
	 * @param searchRequestDto 검색 요청 정보
	 * @return 검색 결과 (장소 및 코스 목록)
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
			searchResultResponse.setSearchPlaces(searchPlaces);
		} else if (targetType == SearchTargetType.ROUTE) {
			List<RouteSearchResponse> searchCourses = new ArrayList<>();
			// 루트(코스) 검색 - 내부 DB만 조회
			searchCourses = routeService.searchCoursesByKeyword(keyword);
			searchResultResponse.setSearchCourses(searchCourses);
		}
		return searchResultResponse;
	}

	// 최근 검색어 조회
	@Transactional(readOnly = true)
	public List<SearchHistoryDto> getRecentSearches(User user) {
		return searchService.getRecentSearchHistory(user);
	}
}
