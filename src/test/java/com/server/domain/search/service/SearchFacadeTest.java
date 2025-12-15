package com.server.domain.search.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.server.domain.map.dto.MapPlaceDto;
import com.server.domain.map.dto.request.KeywordSearchRequest;
import com.server.domain.map.service.MapService;
import com.server.domain.place.dto.PlaceDto;
import com.server.domain.place.service.PlaceService;
import com.server.domain.route.dto.RouteDto;
import com.server.domain.route.service.RouteService;
import com.server.domain.search.dto.SearchHistoryDto;
import com.server.domain.search.dto.SearchResultResponse;
import com.server.domain.search.dto.request.SearchResultRequest;
import com.server.domain.search.entity.SearchPageType;
import com.server.domain.search.entity.SearchTargetType;
import com.server.domain.user.entity.User;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("SearchFacade 단위 테스트")
class SearchFacadeTest {

	@Mock
	private SearchService searchService;

	@Mock
	private PlaceService placeService;

	@Mock
	private RouteService routeService;

	@Mock
	private MapService mapService;

	@InjectMocks
	private SearchFacade searchFacade;

	private User user;
	private PlaceDto placeDto1;
	private PlaceDto placeDto2;
	private RouteDto routeDto1;
	private MapPlaceDto mapPlaceDto1;

	@BeforeEach
	void setUp() {
		user = new User();
		org.springframework.test.util.ReflectionTestUtils.setField(user, "id", 1L);
		org.springframework.test.util.ReflectionTestUtils.setField(user, "nickname", "테스트 유저");

		placeDto1 = PlaceDto.builder()
			.id(1L)
			.name("카페 A")
			.address("서울시 강남구")
			.bookmarked(false)
			.score(4.5)
			.photos(new ArrayList<>())
			.build();

		placeDto2 = PlaceDto.builder()
			.id(2L)
			.name("레스토랑 B")
			.address("서울시 서초구")
			.bookmarked(false)
			.score(4.0)
			.photos(new ArrayList<>())
			.build();

		routeDto1 = RouteDto.builder()
			.id(1L)
			.name("데이트 코스")
			.places(new ArrayList<>())
			.photos(new ArrayList<>())
			.bookmarked(false)
			.build();

		mapPlaceDto1 = MapPlaceDto.builder()
			.id("kakao123")
			.placeName("카카오 장소")
			.addressName("서울시 중구")
			.build();
	}

	@Test
	@DisplayName("장소 검색 - 일반 페이지")
	void search_PlaceTarget_GeneralPage() {
		// given
		String keyword = "카페";
		SearchResultRequest request = new SearchResultRequest();
		request.setKeyword(keyword);
		request.setTargetType(SearchTargetType.PLACE);
		request.setPageType(SearchPageType.MAIN);

		List<PlaceDto> places = List.of(placeDto1, placeDto2);
		when(placeService.searchByKeyword(keyword)).thenReturn(places);

		// when
		SearchResultResponse result = searchFacade.search(user, request);

		// then
		assertAll(
			() -> assertNotNull(result),
			() -> assertNotNull(result.getSearchPlaces()),
			() -> assertEquals(2, result.getSearchPlaces().size()),
			() -> assertEquals("카페 A", result.getSearchPlaces().get(0).getName()),
			() -> assertEquals("레스토랑 B", result.getSearchPlaces().get(1).getName())
		);

		verify(searchService).saveSearchHistory(user, keyword);
		verify(placeService).searchByKeyword(keyword);
		verify(mapService, never()).searchByKeyword(any());
	}

	@Test
	@DisplayName("장소 검색 - 일정 생성 페이지 (카카오 API 포함)")
	void search_PlaceTarget_DateSchedulePage() {
		// given
		String keyword = "카페";
		SearchResultRequest request = new SearchResultRequest();
		request.setKeyword(keyword);
		request.setTargetType(SearchTargetType.PLACE);
		request.setPageType(SearchPageType.DATE_SCHEDULE);

		List<PlaceDto> internalPlaces = new ArrayList<>(List.of(placeDto1));
		List<MapPlaceDto> kakaoPlaces = List.of(mapPlaceDto1);

		when(placeService.searchByKeyword(keyword)).thenReturn(internalPlaces);
		when(mapService.searchByKeyword(any(KeywordSearchRequest.class))).thenReturn(kakaoPlaces);

		// when
		SearchResultResponse result = searchFacade.search(user, request);

		// then
		assertAll(
			() -> assertNotNull(result),
			() -> assertNotNull(result.getSearchPlaces()),
			() -> assertEquals(2, result.getSearchPlaces().size(),
				"내부 DB 결과와 카카오 API 결과가 합쳐져야 함"),
			() -> assertEquals("카페 A", result.getSearchPlaces().get(0).getName()),
			() -> assertEquals("카카오 장소", result.getSearchPlaces().get(1).getName())
		);

		verify(searchService).saveSearchHistory(user, keyword);
		verify(placeService).searchByKeyword(keyword);
		verify(mapService).searchByKeyword(any(KeywordSearchRequest.class));
	}

	@Test
	@DisplayName("루트 검색")
	void search_RouteTarget() {
		// given
		String keyword = "데이트";
		SearchResultRequest request = new SearchResultRequest();
		request.setKeyword(keyword);
		request.setTargetType(SearchTargetType.ROUTE);
		request.setPageType(SearchPageType.MAIN);

		List<RouteDto> routes = List.of(routeDto1);
		when(routeService.searchRoutesByKeyword(keyword, user)).thenReturn(routes);

		// when
		SearchResultResponse result = searchFacade.search(user, request);

		// then
		assertAll(
			() -> assertNotNull(result),
			() -> assertNotNull(result.getSearchRoutes()),
			() -> assertEquals(1, result.getSearchRoutes().size()),
			() -> assertEquals("데이트 코스", result.getSearchRoutes().get(0).getName())
		);

		verify(searchService).saveSearchHistory(user, keyword);
		verify(routeService).searchRoutesByKeyword(keyword, user);
		verify(placeService, never()).searchByKeyword(anyString());
	}

	@Test
	@DisplayName("장소 검색 - 결과 없음")
	void search_PlaceTarget_NoResults() {
		// given
		String keyword = "존재하지않는장소";
		SearchResultRequest request = new SearchResultRequest();
		request.setKeyword(keyword);
		request.setTargetType(SearchTargetType.PLACE);
		request.setPageType(SearchPageType.MAIN);

		when(placeService.searchByKeyword(keyword)).thenReturn(List.of());

		// when
		SearchResultResponse result = searchFacade.search(user, request);

		// then
		assertAll(
			() -> assertNotNull(result),
			() -> assertNotNull(result.getSearchPlaces()),
			() -> assertTrue(result.getSearchPlaces().isEmpty())
		);

		verify(searchService).saveSearchHistory(user, keyword);
		verify(placeService).searchByKeyword(keyword);
	}

	@Test
	@DisplayName("루트 검색 - 결과 없음")
	void search_RouteTarget_NoResults() {
		// given
		String keyword = "존재하지않는루트";
		SearchResultRequest request = new SearchResultRequest();
		request.setKeyword(keyword);
		request.setTargetType(SearchTargetType.ROUTE);
		request.setPageType(SearchPageType.MAIN);

		when(routeService.searchRoutesByKeyword(keyword, user)).thenReturn(List.of());

		// when
		SearchResultResponse result = searchFacade.search(user, request);

		// then
		assertAll(
			() -> assertNotNull(result),
			() -> assertNotNull(result.getSearchRoutes()),
			() -> assertTrue(result.getSearchRoutes().isEmpty())
		);

		verify(searchService).saveSearchHistory(user, keyword);
		verify(routeService).searchRoutesByKeyword(keyword, user);
	}

	@Test
	@DisplayName("최근 검색어 조회")
	void getRecentSearches() {
		// given
		List<SearchHistoryDto> searchHistory = List.of(
			new SearchHistoryDto(1L, "카페", "2024-01-01T00:00:00"),
			new SearchHistoryDto(2L, "레스토랑", "2024-01-02T00:00:00")
		);

		when(searchService.getRecentSearchHistory(user)).thenReturn(searchHistory);

		// when
		List<SearchHistoryDto> result = searchFacade.getRecentSearches(user);

		// then
		assertAll(
			() -> assertNotNull(result),
			() -> assertEquals(2, result.size()),
			() -> assertEquals("카페", result.get(0).getKeyword()),
			() -> assertEquals("레스토랑", result.get(1).getKeyword())
		);

		verify(searchService).getRecentSearchHistory(user);
	}

	@Test
	@DisplayName("최근 검색어 조회 - 결과 없음")
	void getRecentSearches_NoResults() {
		// given
		when(searchService.getRecentSearchHistory(user)).thenReturn(List.of());

		// when
		List<SearchHistoryDto> result = searchFacade.getRecentSearches(user);

		// then
		assertAll(
			() -> assertNotNull(result),
			() -> assertTrue(result.isEmpty())
		);

		verify(searchService).getRecentSearchHistory(user);
	}

	@Test
	@DisplayName("장소 검색 시 검색 히스토리 저장 확인")
	void search_SavesSearchHistory() {
		// given
		String keyword = "테스트 키워드";
		SearchResultRequest request = new SearchResultRequest();
		request.setKeyword(keyword);
		request.setTargetType(SearchTargetType.PLACE);
		request.setPageType(SearchPageType.MAIN);

		when(placeService.searchByKeyword(keyword)).thenReturn(List.of());

		// when
		searchFacade.search(user, request);

		// then
		verify(searchService).saveSearchHistory(user, keyword);
	}

	@Test
	@DisplayName("루트 검색 시 검색 히스토리 저장 확인")
	void search_RouteTarget_SavesSearchHistory() {
		// given
		String keyword = "루트 키워드";
		SearchResultRequest request = new SearchResultRequest();
		request.setKeyword(keyword);
		request.setTargetType(SearchTargetType.ROUTE);
		request.setPageType(SearchPageType.MAIN);

		when(routeService.searchRoutesByKeyword(keyword, user)).thenReturn(List.of());

		// when
		searchFacade.search(user, request);

		// then
		verify(searchService).saveSearchHistory(user, keyword);
	}

	@Test
	@DisplayName("일정 생성 페이지 - 내부 DB만 있을 때")
	void search_DateSchedulePage_OnlyInternalDB() {
		// given
		String keyword = "카페";
		SearchResultRequest request = new SearchResultRequest();
		request.setKeyword(keyword);
		request.setTargetType(SearchTargetType.PLACE);
		request.setPageType(SearchPageType.DATE_SCHEDULE);

		List<PlaceDto> internalPlaces = new ArrayList<>(List.of(placeDto1, placeDto2));

		when(placeService.searchByKeyword(keyword)).thenReturn(internalPlaces);
		when(mapService.searchByKeyword(any(KeywordSearchRequest.class))).thenReturn(List.of());

		// when
		SearchResultResponse result = searchFacade.search(user, request);

		// then
		assertAll(
			() -> assertNotNull(result),
			() -> assertEquals(2, result.getSearchPlaces().size()),
			() -> assertEquals("카페 A", result.getSearchPlaces().get(0).getName()),
			() -> assertEquals("레스토랑 B", result.getSearchPlaces().get(1).getName())
		);

		verify(placeService).searchByKeyword(keyword);
		verify(mapService).searchByKeyword(any(KeywordSearchRequest.class));
	}

	@Test
	@DisplayName("일정 생성 페이지 - 카카오 API만 있을 때")
	void search_DateSchedulePage_OnlyKakaoAPI() {
		// given
		String keyword = "카페";
		SearchResultRequest request = new SearchResultRequest();
		request.setKeyword(keyword);
		request.setTargetType(SearchTargetType.PLACE);
		request.setPageType(SearchPageType.DATE_SCHEDULE);

		List<MapPlaceDto> kakaoPlaces = List.of(mapPlaceDto1);

		when(placeService.searchByKeyword(keyword)).thenReturn(new ArrayList<>());
		when(mapService.searchByKeyword(any(KeywordSearchRequest.class))).thenReturn(kakaoPlaces);

		// when
		SearchResultResponse result = searchFacade.search(user, request);

		// then
		assertAll(
			() -> assertNotNull(result),
			() -> assertEquals(1, result.getSearchPlaces().size()),
			() -> assertEquals("카카오 장소", result.getSearchPlaces().get(0).getName())
		);

		verify(placeService).searchByKeyword(keyword);
		verify(mapService).searchByKeyword(any(KeywordSearchRequest.class));
	}
}
