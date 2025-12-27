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
import com.server.domain.photo.dto.PhotoSummary;
import com.server.domain.photo.service.PhotoService;
import com.server.domain.place.dto.PlaceDto;
import com.server.domain.place.entity.Place;
import com.server.domain.place.service.PlaceService;
import com.server.domain.route.dto.RouteDto;
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
import java.util.List;
import java.util.Map;
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

	@Mock
	private PhotoService photoService;

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
	@DisplayName("루트 검색 - 전체 로직 검증 (모든 의존성 mock)")
	void search_RouteTarget_FullLogic() {
		// given
		String keyword = "데이트";
		SearchResultRequest request = new SearchResultRequest();
		request.setKeyword(keyword);
		request.setTargetType(SearchTargetType.ROUTE);
		request.setPageType(SearchPageType.MAIN);

		// 1. Route 엔티티 생성
		Route route1 = Route.builder()
			.id(1L)
			.name("데이트 코스")
			.build();

		Route route2 = Route.builder()
			.id(2L)
			.name("강남 데이트")
			.build();

		List<Route> routes = List.of(route1, route2);

		// 2. Place 엔티티 생성
		Place place1 = Place.builder()
			.id(10L)
			.name("카페 A")
			.address("서울시 강남구")
			.build();

		Place place2 = Place.builder()
			.id(20L)
			.name("레스토랑 B")
			.address("서울시 서초구")
			.build();

		Place place3 = Place.builder()
			.id(30L)
			.name("영화관 C")
			.address("서울시 강남구")
			.build();

		// 3. RoutePlace 엔티티 생성 (Route와 Place 연결)
		RoutePlace rp1 = RoutePlace.builder()
			.id(1L)
			.route(route1)
			.place(place1)
			.placeOrder(1)
			.build();

		RoutePlace rp2 = RoutePlace.builder()
			.id(2L)
			.route(route1)
			.place(place2)
			.placeOrder(2)
			.build();

		RoutePlace rp3 = RoutePlace.builder()
			.id(3L)
			.route(route2)
			.place(place3)
			.placeOrder(1)
			.build();

		List<RoutePlace> routePlaces = List.of(rp1, rp2, rp3);

		// 4. PhotoSummary 생성
		PhotoSummary photo1 = PhotoSummary.builder()
			.id(1L)
			.imgUrl("http://example.com/photo1.jpg")
			.build();

		PhotoSummary photo2 = PhotoSummary.builder()
			.id(2L)
			.imgUrl("http://example.com/photo2.jpg")
			.build();

		PhotoSummary photo3 = PhotoSummary.builder()
			.id(3L)
			.imgUrl("http://example.com/photo3.jpg")
			.build();

		Map<Long, List<PhotoSummary>> photoSummariesMap = Map.of(
			10L, List.of(photo1),
			20L, List.of(photo2),
			30L, List.of(photo3)
		);

		// 5. Mock 설정
		when(routeService.searchByKeyword(keyword)).thenReturn(routes);
		when(routeService.getPlacesInRoutes(routes)).thenReturn(routePlaces);
		when(photoService.getPlacePhotoSummariesMap(List.of(10L, 20L, 30L)))
			.thenReturn(photoSummariesMap);
		when(routeService.getBookmarkedRoutes(user)).thenReturn(List.of(route1)); // route1만 북마크

		// when
		SearchResultResponse result = searchFacade.search(user, request);

		// then
		assertAll(
			() -> assertNotNull(result),
			() -> assertNotNull(result.getSearchRoutes()),
			() -> assertEquals(2, result.getSearchRoutes().size()),

			// 첫 번째 루트 검증
			() -> assertEquals("데이트 코스", result.getSearchRoutes().get(0).getName()),
			() -> assertEquals(2, result.getSearchRoutes().get(0).getPlaces().size()),
			() -> assertEquals(2, result.getSearchRoutes().get(0).getPhotos().size()),
			() -> assertTrue(result.getSearchRoutes().get(0).isBookmarked(), "route1은 북마크되어야 함"),

			// 두 번째 루트 검증
			() -> assertEquals("강남 데이트", result.getSearchRoutes().get(1).getName()),
			() -> assertEquals(1, result.getSearchRoutes().get(1).getPlaces().size()),
			() -> assertEquals(1, result.getSearchRoutes().get(1).getPhotos().size()),
			() -> assertTrue(!result.getSearchRoutes().get(1).isBookmarked(), "route2는 북마크되지 않아야 함")
		);

		// verify 전체 의존성 호출 확인
		verify(searchService).saveSearchHistory(user, keyword);
		verify(routeService).searchByKeyword(keyword);
		verify(routeService).getPlacesInRoutes(routes);
		verify(photoService).getPlacePhotoSummariesMap(List.of(10L, 20L, 30L));
		verify(routeService).getBookmarkedRoutes(user);
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

		when(routeService.searchByKeyword(keyword)).thenReturn(List.of());

		// when
		SearchResultResponse result = searchFacade.search(user, request);

		// then
		assertAll(
			() -> assertNotNull(result),
			() -> assertNotNull(result.getSearchRoutes()),
			() -> assertTrue(result.getSearchRoutes().isEmpty())
		);

		verify(searchService).saveSearchHistory(user, keyword);
		verify(routeService).searchByKeyword(keyword);
		// 결과가 없으면 다른 서비스 호출 안됨
		verify(routeService, never()).getPlacesInRoutes(any());
		verify(photoService, never()).getPlacePhotoSummariesMap(any());
		verify(routeService, never()).getBookmarkedRoutes(any());
	}

	@Test
	@DisplayName("루트 검색 - 사용자가 null인 경우 (비로그인)")
	void search_RouteTarget_NullUser() {
		// given
		String keyword = "데이트";
		SearchResultRequest request = new SearchResultRequest();
		request.setKeyword(keyword);
		request.setTargetType(SearchTargetType.ROUTE);
		request.setPageType(SearchPageType.MAIN);

		Route route1 = Route.builder()
			.id(1L)
			.name("데이트 코스")
			.build();

		Place place1 = Place.builder()
			.id(10L)
			.name("카페 A")
			.build();

		RoutePlace rp1 = RoutePlace.builder()
			.id(1L)
			.route(route1)
			.place(place1)
			.placeOrder(1)
			.build();

		PhotoSummary photo1 = PhotoSummary.builder()
			.id(1L)
			.imgUrl("http://example.com/photo1.jpg")
			.build();

		when(routeService.searchByKeyword(keyword)).thenReturn(List.of(route1));
		when(routeService.getPlacesInRoutes(List.of(route1))).thenReturn(List.of(rp1));
		when(photoService.getPlacePhotoSummariesMap(List.of(10L)))
			.thenReturn(Map.of(10L, List.of(photo1)));

		// when - user가 null
		SearchResultResponse result = searchFacade.search(null, request);

		// then
		assertAll(
			() -> assertNotNull(result),
			() -> assertEquals(1, result.getSearchRoutes().size()),
			() -> assertTrue(!result.getSearchRoutes().get(0).isBookmarked(),
				"비로그인 사용자는 북마크가 없어야 함")
		);

		verify(routeService, never()).getBookmarkedRoutes(any());
	}

	@Test
	@DisplayName("루트 검색 - 장소가 없는 루트")
	void search_RouteTarget_NoPlaces() {
		// given
		String keyword = "빈코스";
		SearchResultRequest request = new SearchResultRequest();
		request.setKeyword(keyword);
		request.setTargetType(SearchTargetType.ROUTE);
		request.setPageType(SearchPageType.MAIN);

		Route emptyRoute = Route.builder()
			.id(1L)
			.name("빈 코스")
			.build();

		when(routeService.searchByKeyword(keyword)).thenReturn(List.of(emptyRoute));
		when(routeService.getPlacesInRoutes(List.of(emptyRoute))).thenReturn(List.of()); // 빈 리스트
		when(routeService.getBookmarkedRoutes(user)).thenReturn(List.of());

		// when
		SearchResultResponse result = searchFacade.search(user, request);

		// then
		assertAll(
			() -> assertNotNull(result),
			() -> assertEquals(1, result.getSearchRoutes().size()),
			() -> assertEquals("빈 코스", result.getSearchRoutes().get(0).getName()),
			() -> assertTrue(result.getSearchRoutes().get(0).getPlaces().isEmpty()),
			() -> assertTrue(result.getSearchRoutes().get(0).getPhotos().isEmpty())
		);

		// 장소가 없으면 사진 조회도 안됨
		verify(photoService, never()).getPlacePhotoSummariesMap(any());
	}

	@Test
	@DisplayName("루트 검색 - 사진이 없는 장소")
	void search_RouteTarget_NoPhotos() {
		// given
		String keyword = "데이트";
		SearchResultRequest request = new SearchResultRequest();
		request.setKeyword(keyword);
		request.setTargetType(SearchTargetType.ROUTE);
		request.setPageType(SearchPageType.MAIN);

		Route route1 = Route.builder()
			.id(1L)
			.name("데이트 코스")
			.build();

		Place place1 = Place.builder()
			.id(10L)
			.name("카페 A")
			.build();

		RoutePlace rp1 = RoutePlace.builder()
			.id(1L)
			.route(route1)
			.place(place1)
			.placeOrder(1)
			.build();

		when(routeService.searchByKeyword(keyword)).thenReturn(List.of(route1));
		when(routeService.getPlacesInRoutes(List.of(route1))).thenReturn(List.of(rp1));
		when(photoService.getPlacePhotoSummariesMap(List.of(10L)))
			.thenReturn(Map.of()); // 사진 없음
		when(routeService.getBookmarkedRoutes(user)).thenReturn(List.of());

		// when
		SearchResultResponse result = searchFacade.search(user, request);

		// then
		assertAll(
			() -> assertNotNull(result),
			() -> assertEquals(1, result.getSearchRoutes().size()),
			() -> assertEquals(1, result.getSearchRoutes().get(0).getPlaces().size()),
			() -> assertTrue(result.getSearchRoutes().get(0).getPhotos().isEmpty(),
				"사진이 없는 장소는 빈 리스트를 반환해야 함")
		);
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

		when(routeService.searchByKeyword(keyword)).thenReturn(List.of());

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
