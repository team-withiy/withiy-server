package com.server.domain.route.service;

import com.server.domain.photo.entity.Photo;
import com.server.domain.photo.service.PhotoService;
import com.server.domain.place.entity.Place;
import com.server.domain.place.entity.PlaceReview;
import com.server.domain.place.service.PlaceReviewService;
import com.server.domain.route.dto.CategoryInRouteDto;
import com.server.domain.route.dto.PhotoInRouteDto;
import com.server.domain.route.dto.PlaceDetailInRouteDto;
import com.server.domain.route.dto.ReviewInRouteDto;
import com.server.domain.route.dto.ReviewPhotoDto;
import com.server.domain.route.dto.RouteDetailDto;
import com.server.domain.route.dto.UploaderDto;
import com.server.domain.route.entity.Route;
import com.server.domain.route.entity.RoutePlace;
import com.server.domain.user.entity.User;
import com.server.global.constants.PaginationConstants;
import com.server.global.error.code.RouteErrorCode;
import com.server.global.error.exception.BusinessException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouteFacade {

	private final RouteService routeService;
	private final PhotoService photoService;
	private final PlaceReviewService placeReviewService;
	private final static int PLACE_PHOTO_LIMIT = PaginationConstants.PLACE_PHOTO_LIMIT;
	private final static int REVIEW_PHOTO_LIMIT = PaginationConstants.REVIEW_PHOTO_LIMIT;
	private final static int PLACE_REVIEW_LIMIT = PaginationConstants.PLACE_REVIEW_LIMIT;

	/**
	 * 코스 상세 조회
	 *
	 * @param courseId 코스 ID
	 * @return 코스 상세 정보
	 */
	@Transactional(readOnly = true)
	public RouteDetailDto getCourseDetail(Long courseId) {
		// 1. Route 조회
		Route route = routeService.findById(courseId)
			.orElseThrow(() -> new BusinessException(RouteErrorCode.NOT_FOUND));

		// 2. RoutePlace 조회 (placeOrder 순서로 정렬)
		List<RoutePlace> routePlaces = routeService.getPlacesInRoutes(List.of(route)).stream()
			.sorted((rp1, rp2) -> {
				Integer order1 =
					rp1.getPlaceOrder() != null ? rp1.getPlaceOrder() : Integer.MAX_VALUE;
				Integer order2 =
					rp2.getPlaceOrder() != null ? rp2.getPlaceOrder() : Integer.MAX_VALUE;
				return order1.compareTo(order2);
			})
			.collect(Collectors.toList());

		// 3. Place ID 목록 추출
		List<Long> placeIds = routePlaces.stream()
			.map(rp -> rp.getPlace().getId())
			.collect(Collectors.toList());

		if (placeIds.isEmpty()) {
			return RouteDetailDto.builder()
				.routeId(route.getId())
				.name(route.getName())
				.places(List.of())
				.reviews(List.of())
				.build();
		}

		// 4. 각 장소별 사진 조회 (장소당 최신 N개)
		Map<Long, List<PhotoInRouteDto>> photosByPlaceId = new HashMap<>();
		List<Photo> allPhotos = photoService.getLimitedPhotosPerPlace(placeIds,
			PLACE_PHOTO_LIMIT);

		for (Photo photo : allPhotos) {
			Long placeId = photo.getPlace().getId();
			photosByPlaceId.computeIfAbsent(placeId, k -> new ArrayList<>())
				.add(PhotoInRouteDto.from(photo));
		}

		// 5. Place 상세 정보 생성
		List<PlaceDetailInRouteDto> places = routePlaces.stream()
			.map(rp -> {
				Place place = rp.getPlace();
				return PlaceDetailInRouteDto.builder()
					.placeId(place.getId())
					.name(place.getName())
					.category(CategoryInRouteDto.from(place.getCategory()))
					.address(place.getAddress())
					.latitude(place.getLatitude())
					.longitude(place.getLongitude())
					.photos(photosByPlaceId.getOrDefault(place.getId(), List.of()))
					.build();
			})
			.collect(Collectors.toList());

		// 6. 리뷰 조회 (최신 N개)
		List<PlaceReview> placeReviews = placeReviewService.getRecentReviewsByPlaceIds(
			placeIds, PLACE_REVIEW_LIMIT);

		// 7. 리뷰별 사진 조회 - N+1 문제 해결을 위한 배치 조회
		// 리뷰에서 (placeId, userId) 쌍을 추출
		List<Long> reviewPlaceIds = placeReviews.stream()
			.map(review -> review.getPlace().getId())
			.collect(Collectors.toList());

		List<Long> reviewUserIds = placeReviews.stream()
			.map(review -> review.getUser().getId())
			.collect(Collectors.toList());

		// 한 번의 쿼리로 모든 리뷰의 사진 조회
		Map<String, List<Photo>> photosByPlaceAndUser =
			photoService.getPhotosByPlaceAndUserBatch(reviewPlaceIds, reviewUserIds,
				REVIEW_PHOTO_LIMIT);

		// 리뷰 DTO 생성
		List<ReviewInRouteDto> reviews = placeReviews.stream()
			.map(review -> {
				String key = review.getPlace().getId() + ":" + review.getUser().getId();
				List<Photo> reviewPhotos = photosByPlaceAndUser.getOrDefault(key, List.of());

				List<ReviewPhotoDto> photos = reviewPhotos.stream()
					.map(ReviewPhotoDto::from)
					.collect(Collectors.toList());

				return ReviewInRouteDto.builder()
					.reviewId(review.getId())
					.placeId(review.getPlace().getId())
					.placeName(review.getPlace().getName())
					.contents(review.getReview())
					.photos(photos)
					.reviewer(UploaderDto.from(review.getUser()))
					.score(review.getScore())
					.build();
			})
			.collect(Collectors.toList());

		// 8. 최종 응답 생성
		return RouteDetailDto.builder()
			.routeId(route.getId())
			.name(route.getName())
			.places(places)
			.reviews(reviews)
			.build();
	}

	/**
	 * 코스 북마크 추가/삭제 토글
	 *
	 * @param courseId 코스 ID
	 * @param user     사용자 정보
	 * @return 처리 결과 메시지
	 */
	@Transactional
	public String toggleBookmark(Long courseId, User user) {
		Route route = routeService.getRouteById(courseId);
		return routeService.toggleBookmark(route, user);
	}

	/**
	 * 코스 북마크 여부 조회
	 *
	 * @param courseId 코스 ID
	 * @param user     사용자 정보
	 * @return 북마크 여부
	 */
	@Transactional(readOnly = true)
	public boolean isBookmarked(Long courseId, User user) {
		Route route = routeService.getRouteById(courseId);
		return routeService.isBookmarked(route, user);
	}

}
