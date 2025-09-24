package com.server.domain.search.service;

import com.server.domain.folder.service.FolderService;
import com.server.domain.place.dto.PlaceDto;
import com.server.domain.place.entity.Place;
import com.server.domain.place.service.PlaceService;
import com.server.domain.review.service.ReviewService;
import com.server.domain.route.dto.CourseDto;
import com.server.domain.route.service.RouteService;
import com.server.domain.search.dto.BookmarkedPlaceDto;
import com.server.domain.search.dto.SearchHistoryDto;
import com.server.domain.search.dto.SearchRequestDto;
import com.server.domain.search.dto.SearchResponseDto;
import com.server.domain.search.dto.SearchSource;
import com.server.domain.search.dto.response.SearchInitResponse;
import com.server.domain.user.entity.User;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchFacadeService {

	private final SearchService searchService;
	private final PlaceService placeService;
	private final FolderService folderService;
	private final ReviewService reviewService;
	private final RouteService routeService;

	public SearchResponseDto search(User user, SearchRequestDto searchRequestDto) {

		String keyword = searchRequestDto.getKeyword();
		return searchIfKeywordIsNotBlank(user, searchRequestDto);

	}

	private SearchResponseDto searchIfKeywordIsNotBlank(User user,
		SearchRequestDto searchRequestDto) {
		String keyword = searchRequestDto.getKeyword();
		SearchSource source = searchRequestDto.getSource();

		List<PlaceDto> searchPlaces = placeService.searchPlacesByKeyword(source, keyword, user);
		List<CourseDto> searchCourses = routeService.searchCoursesByKeyword(keyword, user);
		return SearchResponseDto.builder()
			.searchPlaces(searchPlaces)
			.searchCourses(searchCourses)
			.build();
	}

	public SearchInitResponse initSearch(User user) {
		// 1. 최근 검색어 조회
		List<SearchHistoryDto> recentKeywords = searchService.getRecentSearchHistory(user);

		// 2. 북마크된 장소 조회
		List<Place> places = folderService.getBookmarkedPlaces(user);
		List<Long> placeIds = places.stream().map(Place::getId).toList();
		Map<Long, Double> placeScoreMap = reviewService.getScoreMapForPlaces(placeIds);
		List<BookmarkedPlaceDto> bookmarkedPlaces = places.stream()
			.map(place -> BookmarkedPlaceDto.of(place, placeScoreMap.get(place.getId())))
			.toList();

		// TODO 3. 북마크된 코스 조회 (추후 구현 예정)
		return SearchInitResponse.builder()
			.recentKeywords(recentKeywords)
			.bookmarkedPlaces(bookmarkedPlaces)
			.bookmarkedCourses(null)
			.build();
	}
}
