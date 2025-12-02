package com.server.domain.review.service;

import com.server.domain.place.entity.Place;
import com.server.domain.review.entity.Review;
import com.server.domain.review.entity.ReviewSortType;
import com.server.domain.review.repository.ReviewRepository;
import com.server.domain.review.repository.projection.PlaceScoreProjection;
import com.server.domain.user.entity.User;
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
		return reviewRepository.findByPlaceIdOrderByUpdatedAt(place.getId(), pageable);
	}

	public CursorPageDto<Review, Long> getReviewsByPlaceWithCursor(Place place,
		ApiCursorPaginationRequest pageRequest, String sortBy) {

		Long cursor = pageRequest.getCursor();
		int limit = pageRequest.getLimit();
		boolean hasNext = false;
		boolean hasPrev = false;
		boolean isPrev = Boolean.TRUE.equals(pageRequest.getPrev());
		long total = reviewRepository.countReviewsByPlaceId(place.getId());
		ReviewSortType sortType = ReviewSortType.of(sortBy);
		Pageable pageable = PageRequest.of(0, limit + 1);
		List<Review> fetched;
		Review cursorReview = null;

		// 1️⃣ 커서가 null이 아닐 때만 DB 조회 시도
		if (cursor != null) {
			cursorReview = reviewRepository.findById(cursor).orElse(null);
		}

		// 2️⃣ 커서가 없거나, 해당 리뷰가 존재하지 않는 경우 → 첫 페이지 처리
		if (cursor == null || cursorReview == null) {
			// 커서가 없으면 첫 페이지: 최신순 limit+1개 조회
			if (sortType == ReviewSortType.LATEST) {
				fetched = reviewRepository.findByPlaceIdOrderByUpdatedAt(place.getId(), pageable);
			} else {
				fetched = reviewRepository.findByPlaceIdOrderByScore(place.getId(), pageable);
			}
			boolean hasMore = fetched.size() > limit;
			hasNext = hasMore;
			hasPrev = false;

			return CursorPaginationUtils.paginate(
				total,
				fetched,
				limit,
				false,
				cursor,
				hasPrev,
				hasNext,
				Review::getId
			);
		}

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
