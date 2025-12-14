package com.server.domain.bookmark.service;

import com.server.domain.bookmark.dto.BookmarkedRouteDto;
import com.server.domain.bookmark.dto.BookmarkedPlaceDto;
import com.server.domain.folder.service.FolderService;
import com.server.domain.photo.dto.PhotoDto;
import com.server.domain.photo.service.PhotoService;
import com.server.domain.place.entity.Place;
import com.server.domain.review.service.ReviewService;
import com.server.domain.route.service.RouteService;
import com.server.domain.user.entity.User;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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

	// 북마크된 루트 조회
	@Transactional(readOnly = true)
	public List<BookmarkedRouteDto> getBookmarkedRoutes(User user) {
		if (user == null) {
			return List.of();
		}

		// 1. 북마크된 루트 목록 조회
		var routes = routeService.getBookmarkedRoutes(user);
		if (routes.isEmpty()) {
			return List.of();
		}

		// 2. 모든 루트의 장소 정보를 한 번의 쿼리로 조회
		var routePlaces = routeService.getPlacesInRoutes(routes);

		// 3. Route ID를 키로 하는 Map으로 그룹화 (순서 유지)
		var routePlaceMap = routePlaces.stream()
			.collect(Collectors.groupingBy(
				rp -> rp.getRoute().getId(),
				LinkedHashMap::new,
				Collectors.mapping(
					rp -> rp.getPlace(),
					Collectors.toList()
				)
			));

		// 4. 각 루트별 대표 사진 조회 및 DTO 생성
		return routes.stream()
			.map(route -> {
				// 루트에 속한 장소 ID 목록 조회 (Map에서 가져오기, 쿼리 발생 없음)
				List<Long> placeIds = routePlaceMap.getOrDefault(route.getId(), List.of())
					.stream()
					.map(Place::getId)
					.toList();
				// 루트 대표 사진 조회
				List<PhotoDto> photos = photoService.getRouteRepresentativePhotos(placeIds);
				return BookmarkedRouteDto.of(route, photos);
			})
			.toList();
	}

}
