package com.server.domain.search.service;

import com.server.domain.map.dto.MapPlaceDto;
import com.server.domain.map.dto.request.KeywordSearchRequest;
import com.server.domain.map.service.MapService;
import com.server.domain.place.dto.PlaceDto;
import com.server.domain.place.service.PlaceService;
import com.server.domain.route.dto.CourseDto;
import com.server.domain.route.service.RouteService;
import com.server.domain.search.dto.SearchHistoryDto;
import com.server.domain.search.dto.SearchResultResponse;
import com.server.domain.search.dto.request.SearchResultRequest;
import com.server.domain.search.entity.SearchSourceType;
import com.server.domain.user.entity.User;
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
	 * @param user
	 * @param searchRequestDto
	 * @return
	 */
	public SearchResultResponse search(User user, SearchResultRequest searchRequestDto) {

		String keyword = searchRequestDto.getKeyword();
		SearchSourceType source = searchRequestDto.getSource();

		List<PlaceDto> searchPlaces = placeService.searchByKeyword(keyword);

		if (searchPlaces.isEmpty() && source == SearchSourceType.DATE_SCHEDULE) {
			List<MapPlaceDto> searchMapPlaces = mapService.searchByKeyword(
				KeywordSearchRequest.builder()
					.query(keyword).build());

			// MapPlaceDto -> PlaceDto 변환
			searchPlaces = searchMapPlaces.stream()
				.map(MapPlaceDto::toPlaceDto)
				.collect(Collectors.toList());

		}
		List<CourseDto> searchCourses = routeService.searchCoursesByKeyword(keyword);
		return SearchResultResponse.builder()
			.searchPlaces(searchPlaces)
			.searchCourses(searchCourses)
			.build();
	}

	// 최근 검색어 조회
	@Transactional(readOnly = true)
	public List<SearchHistoryDto> getRecentSearches(User user) {
		return searchService.getRecentSearchHistory(user);
	}
}
