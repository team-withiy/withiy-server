package com.server.domain.bookmark.service;

import com.server.domain.bookmark.dto.BookmarkedCourseDto;
import com.server.domain.bookmark.dto.BookmarkedPlaceDto;
import com.server.domain.folder.service.FolderService;
import com.server.domain.photo.dto.PhotoDto;
import com.server.domain.photo.service.PhotoService;
import com.server.domain.place.entity.Place;
import com.server.domain.review.service.ReviewService;
import com.server.domain.route.service.RouteService;
import com.server.domain.user.entity.User;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookmarkFacade {


	private final FolderService folderService;
	private final ReviewService reviewService;
	private final RouteService routeService;
	private final PhotoService photoService;

	// 북마크된 장소 조회
	@Transactional(readOnly = true)
	public List<BookmarkedPlaceDto> getBookmarkedPlaces(User user) {
		// 1. 북마크된 장소 조회
		if (user == null) {
			return List.of();
		}

		List<Place> places = folderService.getBookmarkedPlaces(user);
		List<Long> placeIds = places.stream().map(Place::getId).toList();
		// 2. 장소 별 평점 조회
		Map<Long, Double> placeScoreMap = reviewService.getScoreMapForPlaces(placeIds);
		// 3. 장소 별 대표 사진 조회 (공개 상태 최신 사진)
		Map<Long, List<PhotoDto>> placePhotoMap = photoService.getRepresentativePhotosMap(placeIds);

		return places.stream()
			.map(place -> BookmarkedPlaceDto.of(place,
				placeScoreMap.getOrDefault(place.getId(), 0.0),
				placePhotoMap.getOrDefault(place.getId(), List.of())))
			.toList();
	}

	// 북마크된 코스 조회
	@Transactional(readOnly = true)
	public List<BookmarkedCourseDto> getBookmarkedCourses(User user) {
		if (user == null) {
			return List.of();
		}

		return routeService.getBookmarkedRoutes(user).stream()
			.map(route -> {
				// 코스에 속한 장소 ID 목록 조회 (순서대로)
				List<Long> placeIds = routeService.getPlacesInCourse(route).stream()
					.map(Place::getId)
					.toList();
				// 코스 대표 사진 조회
				List<PhotoDto> photos = photoService.getCourseRepresentativePhotos(placeIds);
				return BookmarkedCourseDto.of(route, photos);
			})
			.toList();
	}

}
