package com.server.domain.route.service;

import com.server.domain.photo.dto.PhotoSummary;
import com.server.domain.photo.entity.PhotoType;
import com.server.domain.photo.repository.PhotoRepository;
import com.server.domain.place.entity.Place;
import com.server.domain.route.dto.RouteDto;
import com.server.domain.route.dto.RouteImageDto;
import com.server.domain.route.dto.RoutePlaceSummary;
import com.server.domain.route.entity.Route;
import com.server.domain.route.entity.RouteBookmark;
import com.server.domain.route.entity.RouteImage;
import com.server.domain.route.entity.RoutePlace;
import com.server.domain.route.entity.RouteStatus;
import com.server.domain.route.repository.RouteBookmarkRepository;
import com.server.domain.route.repository.RoutePlaceRepository;
import com.server.domain.route.repository.RouteRepository;
import com.server.domain.user.entity.User;
import com.server.global.dto.ImageResponseDto;
import com.server.global.error.code.RouteErrorCode;
import com.server.global.error.exception.BusinessException;
import com.server.global.service.ImageService;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
@Slf4j
public class RouteService {

	private final RouteRepository routeRepository;
	private final RouteBookmarkRepository routeBookmarkRepository;
	private final RoutePlaceRepository routePlaceRepository;
	private final PhotoRepository photoRepository;
	private final ImageService imageService;

	public void saveRoute(Route route) {
		routeRepository.save(route);
	}

	public void saveRoutePlace(RoutePlace routePlace) {
		routePlaceRepository.save(routePlace);
	}

/*        @Transactional
        public CourseDetailDto getCourseDetail(Long courseId) {
                Course course = courseRepository.findById(courseId).orElseThrow(
                                () -> new BusinessException(CourseErrorCode.NOT_FOUND));

                // 연관된 엔티티 로딩
                course.getCourseImages().size();
                course.getCoursePlaces().size();

                log.info("Course: {}", course); // Course 객체의 상태 확인

                List<CourseImageDto> courseImages = course.getCourseImages().stream()
                                .map(CourseImageDto::from).collect(Collectors.toList());

                log.info("CourseImages: {}", courseImages); // CourseImages 리스트 확인

                List<PlaceDetailDto> placeDetails = course.getCoursePlaces().stream()
                                .map(c -> PlaceDetailDto.from(placeRepository
                                                .findById(c.getPlace().getId())
                                                .orElseThrow(() -> new BusinessException(
                                                                PlaceErrorCode.NOT_FOUND))))
                                .collect(Collectors.toList());

                log.info("PlaceDetails: {}", placeDetails); // PlaceDetails 리스트 확인

                log.info("정보", course.getName(), course.getThumbnail(), courseImages.get(0),
                                placeDetails.get(0));
                return CourseDetailDto.builder().name(course.getName())
                                .thumbnail(course.getThumbnail()).courseImageDtos(courseImages)
                                .placeDetailDtos(placeDetails).build();
        }*/

	/**
	 * 루트 이미지 업로드
	 *
	 * @param routeId 루트 ID
	 * @param file    업로드할 이미지 파일
	 * @return 업로드된 이미지 정보
	 */
	@Transactional
	public RouteImageDto uploadRouteImage(Long routeId, MultipartFile file) {
		Route route = routeRepository.findById(routeId).orElseThrow(
			() -> new BusinessException(RouteErrorCode.NOT_FOUND));

		// 공통 이미지 서비스를 사용하여 이미지 업로드
		ImageResponseDto imageResponseDto =
			imageService.uploadImage(file, "route", routeId);

		// 루트 이미지 엔티티 생성 및 저장
		RouteImage routeImage = new RouteImage();
		// TODO
//		routeImage.setImageUrl(imageResponseDto.getImageUrl());
//		routeImage.setRoute(route);

//                route.getRouteImages().add(routeImage);
		routeRepository.save(route);

		log.info("Route image uploaded for route ID {}: {}", routeId,
			imageResponseDto.getImageUrl());

		return RouteImageDto.builder().imageUrl(routeImage.getImageUrl()).build();
	}

	@Transactional(readOnly = true)
	public List<Route> getBookmarkedRoutes(User user) {
		return routeBookmarkRepository.findByUserWithRoute(user).stream()
			.map(RouteBookmark::getRoute)
			.collect(Collectors.toList());
	}

	/**
	 * 루트 검색
	 *
	 * @param keyword 검색 키워드
	 * @param user    사용자 정보 (북마크 여부 확인용)
	 * @return 검색된 루트 목록
	 */
	@Transactional(readOnly = true)
	public List<RouteDto> searchRoutesByKeyword(String keyword, User user) {
		List<Route> routes = routeRepository.findByNameContainingIgnoreCase(keyword);

		if (routes.isEmpty()) {
			return List.of();
		}

		// 1. 모든 관련 RoutePlace를 한 번의 쿼리로 가져와 Route ID로 그룹화
		Map<Long, List<RoutePlace>> routePlacesByRouteId =
			routePlaceRepository.findByRouteIn(routes).stream()
				.collect(Collectors.groupingBy(rp -> rp.getRoute().getId()));

		// 2. 모든 Route에 포함된 모든 Place의 ID를 수집
		List<Long> allPlaceIds = routePlacesByRouteId.values().stream()
			.flatMap(List::stream)
			.map(rp -> rp.getPlace().getId())
			.distinct()
			.collect(Collectors.toList());

		// 3. 모든 Place의 대표 사진을 한 번의 쿼리로 가져옴 (N+1 문제 해결)
		Map<Long, PhotoSummary> photoSummaryByPlaceId = new HashMap<>();
		if (!allPlaceIds.isEmpty()) {
			photoSummaryByPlaceId = photoRepository
				.findRepresentativePhotosByPlaceIds(allPlaceIds, PhotoType.PUBLIC).stream()
				.collect(Collectors.toMap(
					p -> p.getPlace().getId(),
					PhotoSummary::from,
					(existing, replacement) -> existing
				));
		}

		// 4. 사용자가 북마크한 Route ID를 Set으로 가져와 O(1) 조회 지원
		final Set<Long> bookmarkedRouteIdSet;
		if (user != null) {
			bookmarkedRouteIdSet = routeBookmarkRepository.findByUserWithRoute(user).stream()
				.map(bookmark -> bookmark.getRoute().getId())
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

	public List<Route> getActiveRoutesByKeyword(String keyword) {
		if (keyword == null || keyword.isEmpty()) {
			return routeRepository.findRoutesByStatus(RouteStatus.ACTIVE);
		} else {
			return routeRepository.findRoutesByStatusAndKeyword(RouteStatus.ACTIVE, keyword);
		}
	}

	@Transactional(readOnly = true)
	public long getBookmarkCount(Route route) {
		return routeBookmarkRepository.countByRouteAndNotDeleted(route);
	}

	@Transactional(readOnly = true)
	public List<Place> getPlacesInRoute(Route route) {
		return routePlaceRepository.findPlacesByRoute(route);
	}

	/**
	 * 여러 루트에 속한 모든 장소들을 한 번의 쿼리로 조회
	 *
	 * @param routes 조회할 루트 목록
	 * @return RoutePlace 목록 (루트와 장소 정보 포함)
	 */
	@Transactional(readOnly = true)
	public List<RoutePlace> getPlacesInRoutes(List<Route> routes) {
		if (routes == null || routes.isEmpty()) {
			return List.of();
		}
		return routePlaceRepository.findByRouteIn(routes);
	}

	/**
	 * 코스 대표 이미지(썸네일) 업데이트
	 *
	 * @param courseId 코스 ID
	 * @param file 업로드할 이미지 파일
	 * @return 업데이트된 코스 정보
	 */
        /*@Transactional
        public CourseDetailDto updateCourseThumbnail(Long courseId, MultipartFile file) {
                Course course = courseRepository.findById(courseId).orElseThrow(
                                () -> new BusinessException(CourseErrorCode.NOT_FOUND));

                // 기존 썸네일 이미지가 있으면 삭제
                String oldThumbnail = course.getThumbnail();
                if (oldThumbnail != null && !oldThumbnail.isEmpty()) {
                        imageService.deleteImage(oldThumbnail);
                }

                // 새 이미지 업로드
                ImageResponseDto imageResponseDto =
                                imageService.uploadImage(file, "course", courseId);

                // 코스 썸네일 업데이트
                course.setThumbnail(imageResponseDto.getImageUrl());
                courseRepository.save(course);

                log.info("Course thumbnail updated for course ID {}: {}", courseId,
                                imageResponseDto.getImageUrl());

                return getCourseDetail(courseId);
        }*/
}
