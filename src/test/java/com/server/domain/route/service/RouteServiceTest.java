package com.server.domain.route.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.server.domain.photo.dto.PhotoSummary;
import com.server.domain.photo.entity.Photo;
import com.server.domain.photo.entity.PhotoType;
import com.server.domain.place.entity.Place;
import com.server.domain.route.dto.RouteDto;
import com.server.domain.route.dto.RoutePlaceSummary;
import com.server.domain.route.entity.Route;
import com.server.domain.route.entity.RouteBookmark;
import com.server.domain.route.entity.RoutePlace;
import com.server.domain.route.entity.RouteStatus;
import com.server.domain.route.entity.RouteType;
import com.server.domain.route.repository.RouteBookmarkRepository;
import com.server.domain.route.repository.RoutePlaceRepository;
import com.server.domain.route.repository.RouteRepository;
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
@DisplayName("RouteService 단위 테스트")
class RouteServiceTest {

	@Mock
	private RouteRepository routeRepository;

	@Mock
	private RouteBookmarkRepository routeBookmarkRepository;

	@Mock
	private RoutePlaceRepository routePlaceRepository;

	@Mock
	private com.server.domain.photo.repository.PhotoRepository photoRepository;

	@InjectMocks
	private RouteService routeService;

	private User user;
	private Route route1;
	private Route route2;
	private Place place1;
	private Place place2;
	private Place place3;
	private Photo photo1;
	private Photo photo2;
	private RoutePlace routePlace1;
	private RoutePlace routePlace2;
	private RoutePlace routePlace3;

	@BeforeEach
	void setUp() {
		// User 생성
		user = new User();
		org.springframework.test.util.ReflectionTestUtils.setField(user, "id", 1L);
		org.springframework.test.util.ReflectionTestUtils.setField(user, "nickname", "테스트 유저");

		// Place 생성
		place1 = Place.builder()
			.id(1L)
			.name("장소1")
			.address("주소1")
			.photos(new ArrayList<>())
			.build();

		place2 = Place.builder()
			.id(2L)
			.name("장소2")
			.address("주소2")
			.photos(new ArrayList<>())
			.build();

		place3 = Place.builder()
			.id(3L)
			.name("장소3")
			.address("주소3")
			.photos(new ArrayList<>())
			.build();

		// Photo 생성
		photo1 = Photo.builder()
			.id(1L)
			.imgUrl("http://example.com/photo1.jpg")
			.place(place1)
			.type(PhotoType.PUBLIC)
			.build();

		photo2 = Photo.builder()
			.id(2L)
			.imgUrl("http://example.com/photo2.jpg")
			.place(place2)
			.type(PhotoType.PUBLIC)
			.build();

		place1.getPhotos().add(photo1);
		place2.getPhotos().add(photo2);

		// Route 생성
		route1 = Route.builder()
			.id(1L)
			.name("데이트 코스")
			.status(RouteStatus.ACTIVE)
			.routeType(RouteType.COURSE)
			.createdBy(user)
			.build();

		route2 = Route.builder()
			.id(2L)
			.name("여행 코스")
			.status(RouteStatus.ACTIVE)
			.routeType(RouteType.COURSE)
			.createdBy(user)
			.build();

		// RoutePlace 생성
		routePlace1 = new RoutePlace(route1, place1);
		routePlace1.setId(1L);

		routePlace2 = new RoutePlace(route1, place2);
		routePlace2.setId(2L);

		routePlace3 = new RoutePlace(route2, place3);
		routePlace3.setId(3L);
	}

	@Test
	@DisplayName("키워드로 루트 검색 - 사용자 정보 없음")
	void searchRoutesByKeyword_WithoutUser() {
		// given
		String keyword = "데이트";
		List<Route> routes = List.of(route1);
		List<RoutePlace> routePlaces = List.of(routePlace1, routePlace2);

		when(routeRepository.findByNameContainingIgnoreCase(keyword)).thenReturn(routes);
		when(routePlaceRepository.findByRouteIn(routes)).thenReturn(routePlaces);
		when(photoRepository.findRepresentativePhotosByPlaceIds(List.of(1L, 2L), PhotoType.PUBLIC))
			.thenReturn(List.of(photo1, photo2));

		// when
		List<RouteDto> result = routeService.searchRoutesByKeyword(keyword, null);

		// then
		assertAll(
			() -> assertNotNull(result),
			() -> assertEquals(1, result.size()),
			() -> {
				RouteDto routeDto = result.get(0);
				assertEquals(1L, routeDto.getId());
				assertEquals("데이트 코스", routeDto.getName());
				assertEquals(2, routeDto.getPlaces().size());
				assertEquals(2, routeDto.getPhotos().size());
				assertFalse(routeDto.isBookmarked());
			}
		);

		verify(routeRepository).findByNameContainingIgnoreCase(keyword);
		verify(routePlaceRepository).findByRouteIn(routes);
	}

	@Test
	@DisplayName("키워드로 루트 검색 - 북마크된 루트")
	void searchRoutesByKeyword_WithBookmark() {
		// given
		String keyword = "코스";
		List<Route> routes = List.of(route1, route2);
		List<RoutePlace> routePlaces = List.of(routePlace1, routePlace2, routePlace3);

		RouteBookmark bookmark = new RouteBookmark(1L, route1, user, null);
		List<RouteBookmark> bookmarks = List.of(bookmark);

		when(routeRepository.findByNameContainingIgnoreCase(keyword)).thenReturn(routes);
		when(routePlaceRepository.findByRouteIn(routes)).thenReturn(routePlaces);
		when(routeBookmarkRepository.findByUserWithRoute(user)).thenReturn(bookmarks);
		when(photoRepository.findRepresentativePhotosByPlaceIds(List.of(1L, 2L, 3L), PhotoType.PUBLIC))
			.thenReturn(List.of(photo1, photo2));

		// when
		List<RouteDto> result = routeService.searchRoutesByKeyword(keyword, user);

		// then
		assertAll(
			() -> assertNotNull(result),
			() -> assertEquals(2, result.size()),
			() -> {
				RouteDto routeDto1 = result.get(0);
				assertEquals(1L, routeDto1.getId());
				assertTrue(routeDto1.isBookmarked(), "route1은 북마크되어야 함");
			},
			() -> {
				RouteDto routeDto2 = result.get(1);
				assertEquals(2L, routeDto2.getId());
				assertFalse(routeDto2.isBookmarked(), "route2는 북마크되지 않아야 함");
			}
		);

		verify(routeRepository).findByNameContainingIgnoreCase(keyword);
		verify(routePlaceRepository).findByRouteIn(routes);
		verify(routeBookmarkRepository).findByUserWithRoute(user);
	}

	@Test
	@DisplayName("키워드로 루트 검색 - 검색 결과 없음")
	void searchRoutesByKeyword_NoResults() {
		// given
		String keyword = "존재하지않는루트";
		when(routeRepository.findByNameContainingIgnoreCase(keyword)).thenReturn(List.of());

		// when
		List<RouteDto> result = routeService.searchRoutesByKeyword(keyword, user);

		// then
		assertAll(
			() -> assertNotNull(result),
			() -> assertTrue(result.isEmpty())
		);

		verify(routeRepository).findByNameContainingIgnoreCase(keyword);
	}

	@Test
	@DisplayName("루트 검색 - 장소 목록이 정확히 매핑됨")
	void searchRoutesByKeyword_PlacesMapping() {
		// given
		String keyword = "데이트";
		List<Route> routes = List.of(route1);
		List<RoutePlace> routePlaces = List.of(routePlace1, routePlace2);

		when(routeRepository.findByNameContainingIgnoreCase(keyword)).thenReturn(routes);
		when(routePlaceRepository.findByRouteIn(routes)).thenReturn(routePlaces);
		when(photoRepository.findRepresentativePhotosByPlaceIds(List.of(1L, 2L), PhotoType.PUBLIC))
			.thenReturn(List.of(photo1, photo2));

		// when
		List<RouteDto> result = routeService.searchRoutesByKeyword(keyword, null);

		// then
		RouteDto routeDto = result.get(0);
		List<RoutePlaceSummary> places = routeDto.getPlaces();

		assertAll(
			() -> assertEquals(2, places.size()),
			() -> assertEquals(1L, places.get(0).getPlaceId()),
			() -> assertEquals("장소1", places.get(0).getName()),
			() -> assertEquals(2L, places.get(1).getPlaceId()),
			() -> assertEquals("장소2", places.get(1).getName())
		);
	}

	@Test
	@DisplayName("루트 검색 - 사진 목록이 정확히 매핑됨")
	void searchRoutesByKeyword_PhotosMapping() {
		// given
		String keyword = "데이트";
		List<Route> routes = List.of(route1);
		List<RoutePlace> routePlaces = List.of(routePlace1, routePlace2);

		when(routeRepository.findByNameContainingIgnoreCase(keyword)).thenReturn(routes);
		when(routePlaceRepository.findByRouteIn(routes)).thenReturn(routePlaces);
		when(photoRepository.findRepresentativePhotosByPlaceIds(List.of(1L, 2L), PhotoType.PUBLIC))
			.thenReturn(List.of(photo1, photo2));

		// when
		List<RouteDto> result = routeService.searchRoutesByKeyword(keyword, null);

		// then
		RouteDto routeDto = result.get(0);
		List<PhotoSummary> photos = routeDto.getPhotos();

		assertAll(
			() -> assertEquals(2, photos.size()),
			() -> assertEquals(1L, photos.get(0).getPhotoId()),
			() -> assertEquals("http://example.com/photo1.jpg", photos.get(0).getImageUrl()),
			() -> assertEquals(2L, photos.get(1).getPhotoId()),
			() -> assertEquals("http://example.com/photo2.jpg", photos.get(1).getImageUrl())
		);
	}

	@Test
	@DisplayName("루트 검색 - 사진이 없는 장소는 제외됨")
	void searchRoutesByKeyword_PlacesWithoutPhotosExcluded() {
		// given
		String keyword = "여행";
		List<Route> routes = List.of(route2);
		List<RoutePlace> routePlaces = List.of(routePlace3); // place3은 사진 없음

		when(routeRepository.findByNameContainingIgnoreCase(keyword)).thenReturn(routes);
		when(routePlaceRepository.findByRouteIn(routes)).thenReturn(routePlaces);
		when(photoRepository.findRepresentativePhotosByPlaceIds(List.of(3L), PhotoType.PUBLIC))
			.thenReturn(List.of()); // 사진 없음

		// when
		List<RouteDto> result = routeService.searchRoutesByKeyword(keyword, null);

		// then
		RouteDto routeDto = result.get(0);
		assertAll(
			() -> assertEquals(1, routeDto.getPlaces().size()),
			() -> assertTrue(routeDto.getPhotos().isEmpty(), "사진이 없는 장소는 이미지 목록에서 제외됨")
		);
	}

	@Test
	@DisplayName("루트 검색 - 최대 10개의 이미지만 반환")
	void searchRoutesByKeyword_MaximumTenPhotos() {
		// given
		String keyword = "테스트";
		List<Route> routes = List.of(route1);
		List<RoutePlace> routePlaces = new ArrayList<>();

		// 12개의 장소와 사진 생성
		for (int i = 1; i <= 12; i++) {
			Place place = Place.builder()
				.id((long) i)
				.name("장소" + i)
				.photos(new ArrayList<>())
				.build();

			Photo photo = Photo.builder()
				.id((long) i)
				.imgUrl("http://example.com/photo" + i + ".jpg")
				.place(place)
				.type(PhotoType.PUBLIC)
				.build();

			place.getPhotos().add(photo);

			RoutePlace routePlace = new RoutePlace(route1, place);
			routePlace.setId((long) i);
			routePlaces.add(routePlace);
		}

		when(routeRepository.findByNameContainingIgnoreCase(keyword)).thenReturn(routes);
		when(routePlaceRepository.findByRouteIn(routes)).thenReturn(routePlaces);
		
		// 12개 장소의 대표 사진 mock (실제로는 10개만 반환됨 - 서비스 로직에서 제한)
		List<Photo> mockPhotos = new ArrayList<>();
		for (int i = 1; i <= 12; i++) {
			Place place = Place.builder().id((long) i).name("장소" + i).build();
			Photo photo = Photo.builder()
				.id((long) i)
				.imgUrl("http://example.com/photo" + i + ".jpg")
				.place(place)
				.type(PhotoType.PUBLIC)
				.build();
			mockPhotos.add(photo);
		}
		when(photoRepository.findRepresentativePhotosByPlaceIds(
			List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L), PhotoType.PUBLIC))
			.thenReturn(mockPhotos);

		// when
		List<RouteDto> result = routeService.searchRoutesByKeyword(keyword, null);

		// then
		RouteDto routeDto = result.get(0);
		assertAll(
			() -> assertEquals(12, routeDto.getPlaces().size(), "모든 장소가 포함되어야 함"),
			() -> assertEquals(10, routeDto.getPhotos().size(), "최대 10개의 사진만 포함되어야 함")
		);
	}

	@Test
	@DisplayName("활성 상태 루트 조회 - 키워드 없음")
	void getActiveRoutesByKeyword_NoKeyword() {
		// given
		List<Route> routes = List.of(route1, route2);
		when(routeRepository.findRoutesByStatus(RouteStatus.ACTIVE)).thenReturn(routes);

		// when
		List<Route> result = routeService.getActiveRoutesByKeyword(null);

		// then
		assertAll(
			() -> assertNotNull(result),
			() -> assertEquals(2, result.size())
		);

		verify(routeRepository).findRoutesByStatus(RouteStatus.ACTIVE);
	}

	@Test
	@DisplayName("활성 상태 루트 조회 - 키워드 있음")
	void getActiveRoutesByKeyword_WithKeyword() {
		// given
		String keyword = "데이트";
		List<Route> routes = List.of(route1);
		when(routeRepository.findRoutesByStatusAndKeyword(RouteStatus.ACTIVE, keyword))
			.thenReturn(routes);

		// when
		List<Route> result = routeService.getActiveRoutesByKeyword(keyword);

		// then
		assertAll(
			() -> assertNotNull(result),
			() -> assertEquals(1, result.size()),
			() -> assertEquals("데이트 코스", result.get(0).getName())
		);

		verify(routeRepository).findRoutesByStatusAndKeyword(RouteStatus.ACTIVE, keyword);
	}

	@Test
	@DisplayName("북마크된 루트 조회")
	void getBookmarkedRoutes() {
		// given
		RouteBookmark bookmark1 = new RouteBookmark(1L, route1, user, null);

		RouteBookmark bookmark2 = new RouteBookmark(2L, route2, user, null);

		List<RouteBookmark> bookmarks = List.of(bookmark1, bookmark2);
		when(routeBookmarkRepository.findByUserWithRoute(user)).thenReturn(bookmarks);

		// when
		List<Route> result = routeService.getBookmarkedRoutes(user);

		// then
		assertAll(
			() -> assertNotNull(result),
			() -> assertEquals(2, result.size()),
			() -> assertEquals(route1.getId(), result.get(0).getId()),
			() -> assertEquals(route2.getId(), result.get(1).getId())
		);

		verify(routeBookmarkRepository).findByUserWithRoute(user);
	}

	@Test
	@DisplayName("루트의 북마크 수 조회")
	void getBookmarkCount() {
		// given
		long expectedCount = 5L;
		when(routeBookmarkRepository.countByRouteAndNotDeleted(route1)).thenReturn(expectedCount);

		// when
		long result = routeService.getBookmarkCount(route1);

		// then
		assertEquals(expectedCount, result);
		verify(routeBookmarkRepository).countByRouteAndNotDeleted(route1);
	}

	@Test
	@DisplayName("루트에 속한 장소 조회")
	void getPlacesInRoute() {
		// given
		List<Place> places = List.of(place1, place2);
		when(routePlaceRepository.findPlacesByRoute(route1)).thenReturn(places);

		// when
		List<Place> result = routeService.getPlacesInRoute(route1);

		// then
		assertAll(
			() -> assertNotNull(result),
			() -> assertEquals(2, result.size()),
			() -> assertEquals(place1.getId(), result.get(0).getId()),
			() -> assertEquals(place2.getId(), result.get(1).getId())
		);

		verify(routePlaceRepository).findPlacesByRoute(route1);
	}

	@Test
	@DisplayName("여러 루트에 속한 장소 조회")
	void getPlacesInRoutes() {
		// given
		List<Route> routes = List.of(route1, route2);
		List<RoutePlace> routePlaces = List.of(routePlace1, routePlace2, routePlace3);
		when(routePlaceRepository.findByRouteIn(routes)).thenReturn(routePlaces);

		// when
		List<RoutePlace> result = routeService.getPlacesInRoutes(routes);

		// then
		assertAll(
			() -> assertNotNull(result),
			() -> assertEquals(3, result.size())
		);

		verify(routePlaceRepository).findByRouteIn(routes);
	}

	@Test
	@DisplayName("여러 루트 조회 - 빈 리스트")
	void getPlacesInRoutes_EmptyList() {
		// given & when
		List<RoutePlace> result = routeService.getPlacesInRoutes(List.of());

		// then
		assertAll(
			() -> assertNotNull(result),
			() -> assertTrue(result.isEmpty())
		);
	}

	@Test
	@DisplayName("여러 루트 조회 - null")
	void getPlacesInRoutes_Null() {
		// given & when
		List<RoutePlace> result = routeService.getPlacesInRoutes(null);

		// then
		assertAll(
			() -> assertNotNull(result),
			() -> assertTrue(result.isEmpty())
		);
	}
}
