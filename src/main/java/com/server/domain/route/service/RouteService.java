package com.server.domain.route.service;

import com.server.domain.place.entity.Place;
import com.server.domain.route.dto.RouteImageDto;
import com.server.domain.route.dto.response.RouteSearchResponse;
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
import com.server.global.error.code.CourseErrorCode;
import com.server.global.error.exception.BusinessException;
import com.server.global.service.ImageService;
import java.util.List;
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
	 * 코스 이미지 업로드
	 *
	 * @param courseId 코스 ID
	 * @param file     업로드할 이미지 파일
	 * @return 업로드된 이미지 정보
	 */
	@Transactional
	public RouteImageDto uploadCourseImage(Long courseId, MultipartFile file) {
		Route route = routeRepository.findById(courseId).orElseThrow(
			() -> new BusinessException(CourseErrorCode.NOT_FOUND));

		// 공통 이미지 서비스를 사용하여 이미지 업로드
		ImageResponseDto imageResponseDto =
			imageService.uploadImage(file, "course", courseId);

		// 코스 이미지 엔티티 생성 및 저장
		RouteImage routeImage = new RouteImage();
		// TODO
//		routeImage.setImageUrl(imageResponseDto.getImageUrl());
//		routeImage.setRoute(route);

//                course.getCourseImages().add(courseImage);
		routeRepository.save(route);

		log.info("Course image uploaded for course ID {}: {}", courseId,
			imageResponseDto.getImageUrl());

		return RouteImageDto.builder().imageUrl(routeImage.getImageUrl()).build();
	}

	@Transactional(readOnly = true)
	public List<Route> getBookmarkedRoutes(User user) {
		return routeBookmarkRepository.findByUserWithCourse(user).stream()
			.map(RouteBookmark::getRoute)
			.collect(Collectors.toList());
	}

	/**
	 * 코스 검색
	 *
	 * @param keyword 검색 키워드
	 * @return 검색된 코스 목록
	 */
	@Transactional(readOnly = true)
	public List<RouteSearchResponse> searchCoursesByKeyword(String keyword) {
		List<Route> routes = routeRepository.findByNameContainingIgnoreCase(keyword);
		return routes.stream()
			.map(route -> RouteSearchResponse.of(route, List.of(), List.of()))
			.collect(Collectors.toList());
	}

	public List<Route> getActiveCoursesByKeyword(String keyword) {
		if (keyword == null || keyword.isEmpty()) {
			return routeRepository.findCoursesByStatus(RouteStatus.ACTIVE);
		} else {
			return routeRepository.findCoursesByStatusAndKeyword(RouteStatus.ACTIVE, keyword);
		}
	}

	@Transactional(readOnly = true)
	public long getBookmarkCount(Route route) {
		return routeBookmarkRepository.countByRouteAndNotDeleted(route);
	}

	@Transactional(readOnly = true)
	public List<Place> getPlacesInCourse(Route route) {
		return routePlaceRepository.findPlacesByCourse(route);
	}

	/**
	 * 여러 코스에 속한 모든 장소들을 한 번의 쿼리로 조회
	 *
	 * @param routes 조회할 코스 목록
	 * @return RoutePlace 목록 (코스와 장소 정보 포함)
	 */
	@Transactional(readOnly = true)
	public List<RoutePlace> getPlacesInCourses(List<Route> routes) {
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
