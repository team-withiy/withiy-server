package com.server.domain.review.service;

import com.server.domain.place.entity.Place;
import com.server.domain.review.entity.Review;
import com.server.domain.review.repository.ReviewRepository;
import com.server.domain.review.repository.projection.PlaceScoreProjection;
import com.server.domain.user.entity.User;
import com.server.global.error.code.ReviewErrorCode;
import com.server.global.error.exception.BusinessException;
import com.server.global.pagination.dto.ApiCursorPaginationRequest;
import com.server.global.pagination.dto.CursorPageDto;
import com.server.global.pagination.utils.CursorPaginationUtils;
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
	private final static int PLACE_DEFAULT_REVIEW_LIMIT = 4;

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
	 * 특정 장소에 대한 리뷰를 최신순, 평점순으로 정렬하여 limit 개수만큼 조회
	 *
	 * @param place
	 * @return
	 */
	@Transactional
	public List<Review> getTopReviewsByPlace(Place place) {
		Pageable pageable = PageRequest.of(0, PLACE_DEFAULT_REVIEW_LIMIT);
		return reviewRepository.findByPlaceId(place.getId(), pageable);
	}

	public CursorPageDto<Review, Long> getReviewsByPlaceWithCursor(Place place,
		ApiCursorPaginationRequest pageRequest) {

		Long cursor = pageRequest.getCursor();
		int limit = pageRequest.getLimit();
		boolean hasNext = false;
		boolean hasPrev = false;
		long total = reviewRepository.countReviewsByPlaceId(place.getId());

		Review cursorReview = reviewRepository.findById(cursor)
			.orElseThrow(() -> new BusinessException(ReviewErrorCode.REVIEW_NOT_FOUND));

		Pageable pageable = PageRequest.of(0, limit + 1);
		List<Review> fetched;

		if (Boolean.TRUE.equals(pageRequest.getPrev())) {
			fetched = reviewRepository.findPrevReviewsByPlaceId(place.getId(),
				cursorReview.getUpdatedAt(),
				cursorReview.getScore(),
				pageable);

			Collections.reverse(fetched);
			boolean hasMore = fetched.size() > limit;
			hasPrev = hasMore;
			hasNext = reviewRepository.existsNextReviewByPlaceId(place.getId(),
				cursorReview.getUpdatedAt(),
				cursorReview.getScore());
		} else {

			fetched = reviewRepository.findNextReviewsByPlaceId(place.getId(),
				cursorReview.getUpdatedAt(),
				cursorReview.getScore(),
				pageable);
			boolean hasMore = fetched.size() > limit;
			hasNext = hasMore;
			hasPrev = reviewRepository.existsPrevReviewByPlaceId(place.getId(),
				cursorReview.getUpdatedAt(),
				cursorReview.getScore());
		}

		return CursorPaginationUtils.paginate(
			total,
			fetched,
			limit,
			Boolean.TRUE.equals(pageRequest.getPrev()),
			cursor,
			hasPrev,
			hasNext,
			Review::getId
		);
	}

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
