package com.server.domain.review.service;

import com.server.domain.place.entity.Place;
import com.server.domain.review.entity.Review;
import com.server.domain.review.entity.ReviewSortType;
import com.server.domain.review.executor.ReviewCursorQueryExecutor;
import com.server.domain.review.repository.ReviewRepository;
import com.server.domain.review.repository.projection.PlaceScoreProjection;
import com.server.domain.user.entity.User;
import com.server.global.pagination.dto.ApiCursorPaginationRequest;
import com.server.global.pagination.dto.CursorPageDto;
import com.server.global.pagination.executor.CursorQueryExecutor;
import com.server.global.pagination.service.PaginationService;
import com.server.global.pagination.strategy.PaginationContext;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final PaginationService paginationService;
	private final static int PLACE_DEFAULT_REVIEW_LIMIT = 5;

	@Transactional
	public Review save(Place place, User user, String contents, Long score) {
		Review review = Review.builder()
			.place(place)
			.user(user)
			.contents(contents)
			.score(score)
			.build();

		return reviewRepository.save(review);
	}

	/**
	 * 특정 장소에 대한 리뷰를 최신순으로 정렬하여 limit 개수만큼 조회
	 *
	 * @param place 장소
	 * @return 리뷰 목록
	 */
	@Transactional(readOnly = true)
	public List<Review> getTopReviewsByPlace(Place place) {
		Pageable pageable = PageRequest.of(0, PLACE_DEFAULT_REVIEW_LIMIT);
		return reviewRepository.findByPlaceIdOrderByLatest(place.getId(), pageable);
	}

	/**
	 * 커서 기반 페이징으로 리뷰 조회
	 *
	 * <p>정렬 방식:
	 * <ul>
	 *   <li>LATEST: 최근 생성순 (ID DESC)</li>
	 *   <li>SCORE: 평점순 (score DESC, id DESC)</li>
	 * </ul>
	 *
	 * @param place       장소
	 * @param pageRequest 페이징 요청
	 * @return 페이징된 리뷰 목록
	 */
	@Transactional(readOnly = true)
	public CursorPageDto<Review, Long> getReviewsByPlaceWithCursor(
		Place place, ApiCursorPaginationRequest pageRequest) {

		String sortBy = pageRequest.getSortBy();
		ReviewSortType sortType = ReviewSortType.of(sortBy);

		// 1. Executor 생성
		CursorQueryExecutor<Review, Long> executor =
			new ReviewCursorQueryExecutor(reviewRepository, place.getId(), sortType);

		// 2. Context 구성
		PaginationContext<Review, Long> context = PaginationContext.<Review, Long>builder()
			.request(pageRequest)
			.queryExecutor(executor)
			.idExtractor(Review::getId)
			.build();

		// 3. 페이징 실행
		return paginationService.paginate(context);
	}

	@Transactional(readOnly = true)
	public Map<Long, Double> getScoreMapForPlaces(List<Long> placeIds) {
		if (placeIds == null || placeIds.isEmpty()) {
			return Collections.emptyMap();
		}
		return reviewRepository.findAvgScoreByPlaceIds(placeIds).stream()
			.collect(Collectors.toMap(
				PlaceScoreProjection::getPlaceId,
				PlaceScoreProjection::getAvgScore
			));
	}
}
