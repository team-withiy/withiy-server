package com.server.domain.review.service;

import com.server.domain.place.entity.Place;
import com.server.domain.review.entity.Review;
import com.server.domain.review.entity.ReviewSortType;
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
		ApiCursorPaginationRequest pageRequest, String sortBy) {

		Long cursor = pageRequest.getCursor();
		int limit = pageRequest.getLimit();
		boolean hasNext = false;
		boolean hasPrev = false;
		boolean isPrev = Boolean.TRUE.equals(pageRequest.getPrev());
		long total = reviewRepository.countReviewsByPlaceId(place.getId());
		ReviewSortType sortType = ReviewSortType.valueOf(sortBy)

		Review cursorReview = reviewRepository.findById(cursor)
			.orElseThrow(() -> new BusinessException(ReviewErrorCode.REVIEW_NOT_FOUND));

		Pageable pageable = PageRequest.of(0, limit + 1);
		List<Review> fetched;

		if (isPrev) {
			if (sortType == ReviewSortType.SCORE) {
				fetched = reviewRepository.findPrevReviewsByPlaceIdOrderByScore(
					place.getId(),
					cursorReview.getScore(),
					cursorReview.getUpdatedAt(),
					pageable
				);
			} else {
				fetched = reviewRepository.findPrevReviewsByPlaceIdOrderByUpdatedAt(
					place.getId(),
					cursorReview.getUpdatedAt(),
					cursorReview.getScore(),
					pageable
				);
			}

			Collections.reverse(fetched);
		} else {
			if (sortType == ReviewSortType.SCORE) {
				fetched = reviewRepository.findNextReviewsByPlaceIdOrderByScore(
					place.getId(),
					cursorReview.getScore(),
					cursorReview.getUpdatedAt(),
					pageable
				);
			} else {
				fetched = reviewRepository.findNextReviewsByPlaceIdOrderByUpdatedAt(
					place.getId(),
					cursorReview.getUpdatedAt(),
					cursorReview.getScore(),
					pageable
				);
			}
		}

		boolean hasMore = fetched.size() > limit;

		if (sortType == ReviewSortType.SCORE) {
			hasNext = reviewRepository.existsNextReviewByPlaceIdOrderByScore(
				place.getId(),
				cursorReview.getScore(),
				cursorReview.getUpdatedAt()
			);
			hasPrev = reviewRepository.existsPrevReviewByPlaceIdOrderByScore(
				place.getId(),
				cursorReview.getScore(),
				cursorReview.getUpdatedAt()
			);
		} else {
			hasNext = reviewRepository.existsNextReviewByPlaceIdOrderByUpdatedAt(
				place.getId(),
				cursorReview.getUpdatedAt(),
				cursorReview.getScore()
			);
			hasPrev = reviewRepository.existsPrevReviewByPlaceIdOrderByUpdatedAt(
				place.getId(),
				cursorReview.getUpdatedAt(),
				cursorReview.getScore()
			);
		}

		return CursorPaginationUtils.paginate(
			total,
			fetched,
			limit,
			isPrev,
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
