package com.server.domain.admin.service;

import com.server.domain.admin.dto.ActiveContentsResponse;
import com.server.domain.admin.dto.ActiveCourseDto;
import com.server.domain.admin.dto.ActivePlaceDto;
import com.server.domain.category.dto.CategoryDto;
import com.server.domain.category.entity.Category;
import com.server.domain.category.service.CategoryService;
import com.server.domain.folder.service.FolderService;
import com.server.domain.photo.entity.PhotoType;
import com.server.domain.photo.service.PhotoService;
import com.server.domain.place.dto.CreatePlaceDto;
import com.server.domain.place.dto.CreatePlaceResponse;
import com.server.domain.place.dto.PlaceStatus;
import com.server.domain.place.entity.Place;
import com.server.domain.place.service.PlaceService;
import com.server.domain.route.entity.Route;
import com.server.domain.route.service.RouteService;
import com.server.domain.user.entity.User;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminFacade {

	private final PlaceService placeService;
	private final CategoryService categoryService;
	private final PhotoService photoService;
	private final RouteService routeService;
	private final FolderService folderService;
	private final static int PLACE_DEFAULT_PHOTO_LIMIT = 30;

	@Transactional(readOnly = true)
	public ActiveContentsResponse getActiveContents(String categoryName, String keyword) {

		// 카테고리 이름으로 카테고리 조회
		Category category = categoryService.getCategoryByName(categoryName);

		// 카테고리와 키워드로 활성화된 장소 조회
		List<Place> places = placeService.getActivePlacesByCategoryAndKeyword(category, keyword);

		// ActivePlaceDto 리스트 생성
		List<ActivePlaceDto> activePlaces = getActivePlaces(places, category);

		// ActiveCourseDto 리스트 생성
		List<ActiveCourseDto> activeCourses = getActiveCourses(keyword);

		// ActiveContentsResponse
		return ActiveContentsResponse.builder()
			.places(activePlaces)
			.courses(activeCourses)
			.build();
	}

	private List<ActiveCourseDto> getActiveCourses(String keyword) {
		return routeService.getActiveCoursesByKeyword(keyword)
			.stream()
			.map(this::convertToActiveCourseDto)
			.toList();
	}

	private ActiveCourseDto convertToActiveCourseDto(Route route) {
		List<Place> places = routeService.getPlacesInCourse(route);
		List<String> placeNames = places.stream().map(Place::getName).collect(Collectors.toList());
		List<Long> placeIds = places.stream().map(Place::getId).collect(Collectors.toList());
		List<String> photoUrls = photoService.getLimitedPhotoUrlsByPlaceIds(placeIds,
			PLACE_DEFAULT_PHOTO_LIMIT);

		return ActiveCourseDto.builder()
			.courseId(route.getId())
			.courseName(route.getName())
			.placeNames(placeNames)
			.bookmarkCount(routeService.getBookmarkCount(route))
			.photoUrls(photoUrls)
			.build();
	}

	List<ActivePlaceDto> getActivePlaces(List<Place> places, Category category) {
		// 각 Place에 대해 북마크 수, 좋아요 수, 이미지 URL 목록을 조회하여 ActivePlaceDto 생성
		List<ActivePlaceDto> activePlaces = new ArrayList<>();
		for (Place place : places) {
			long score = place.getScore();

			// 사진 URL 목록 조회
			List<String> photoUrls = photoService.getLimitedPhotoUrlsByPlaceId(place.getId(),
				PLACE_DEFAULT_PHOTO_LIMIT);

			long bookmarkCount = folderService.countBookmarkedByPlaceId(place.getId());

			activePlaces.add(ActivePlaceDto.builder()
				.placeId(place.getId())
				.placeName(place.getName())
				.placeAddress(place.getAddress())
				.createdByAdmin(place.isCreatedByAdmin())
				.bookmarkCount(bookmarkCount)
				.score(score)
				.photoUrls(photoUrls)
				.placeCategory(CategoryDto.from(category))
				.build());
		}
		return activePlaces;
	}

	@Transactional
	public CreatePlaceResponse registerPlace(User user, CreatePlaceDto createPlaceDto) {
		Category category = categoryService.getCategoryByName(createPlaceDto.getCategoryName());
		Place place = Place.builder()
			.name(createPlaceDto.getName())
			.region1depth(createPlaceDto.getRegion1depth())
			.region2depth(createPlaceDto.getRegion2depth())
			.region3depth(createPlaceDto.getRegion3depth())
			.address(createPlaceDto.getAddress())
			.latitude(createPlaceDto.getLatitude())
			.longitude(createPlaceDto.getLongitude())
			.score(0L)
			.user(user)
			.category(category)
			.status(PlaceStatus.ACTIVE)
			.build();

		Place savedPlace = placeService.save(place);
		photoService.uploadPhotos(user, place, createPlaceDto.getImageUrls(), PhotoType.PUBLIC);

		return CreatePlaceResponse.from(savedPlace);
	}
}
