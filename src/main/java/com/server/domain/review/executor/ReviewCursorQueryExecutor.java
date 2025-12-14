package com.server.domain.review.executor;

import com.server.domain.review.entity.Review;
import com.server.domain.review.entity.ReviewSortType;
import com.server.domain.review.repository.ReviewRepository;
import com.server.global.pagination.executor.CursorQueryExecutor;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Review 엔티티를 위한 커서 쿼리 실행자
 * 
 * <p>정렬 방식에 따라 다른 쿼리 메서드를 호출합니다:
 * <ul>
 *   <li>LATEST: ID 기준 정렬 (최근 생성순)</li>
 *   <li>SCORE: 평점 + ID 복합 정렬</li>
 * </ul>
 */
@RequiredArgsConstructor
public class ReviewCursorQueryExecutor implements CursorQueryExecutor<Review, Long> {

	private final ReviewRepository reviewRepository;
	private final Long placeId;
	private final ReviewSortType sortType;

	@Override
	public List<Review> findNext(Long cursor, int limit) {
		Pageable pageable = PageRequest.of(0, limit);
		
		if (sortType == ReviewSortType.LATEST) {
			// Latest: ID로 정렬
			if (cursor == null) {
				return reviewRepository.findByPlaceIdOrderByLatest(placeId, pageable);
			}
			return reviewRepository.findNextByPlaceIdOrderByLatest(placeId, cursor, pageable);
			
		} else {
			// Score: 평점 + ID로 정렬
			if (cursor == null) {
				return reviewRepository.findByPlaceIdOrderByScore(placeId, pageable);
			}
			
			// 커서 리뷰를 조회하여 score 추출
			Review cursorReview = reviewRepository.findById(cursor).orElse(null);
			if (cursorReview == null) {
				return List.of();
			}
			
			return reviewRepository.findNextByPlaceIdOrderByScore(
				placeId, 
				cursorReview.getScore(), 
				cursor, 
				pageable
			);
		}
	}

	@Override
	public List<Review> findPrev(Long cursor, int limit) {
		if (cursor == null) {
			return List.of();
		}
		
		Pageable pageable = PageRequest.of(0, limit);
		
		if (sortType == ReviewSortType.LATEST) {
			// Latest: ID로 정렬
			return reviewRepository.findPrevByPlaceIdOrderByLatest(placeId, cursor, pageable);
			
		} else {
			// Score: 평점 + ID로 정렬
			Review cursorReview = reviewRepository.findById(cursor).orElse(null);
			if (cursorReview == null) {
				return List.of();
			}
			
			return reviewRepository.findPrevByPlaceIdOrderByScore(
				placeId, 
				cursorReview.getScore(), 
				cursor, 
				pageable
			);
		}
	}

	@Override
	public boolean existsNext(Long cursor) {
		if (cursor == null) {
			return false;
		}
		
		if (sortType == ReviewSortType.LATEST) {
			return reviewRepository.existsNextByPlaceIdOrderByLatest(placeId, cursor);
		} else {
			Review cursorReview = reviewRepository.findById(cursor).orElse(null);
			if (cursorReview == null) {
				return false;
			}
			return reviewRepository.existsNextByPlaceIdOrderByScore(
				placeId, 
				cursorReview.getScore(), 
				cursor
			);
		}
	}

	@Override
	public boolean existsPrev(Long cursor) {
		if (cursor == null) {
			return false;
		}
		
		if (sortType == ReviewSortType.LATEST) {
			return reviewRepository.existsPrevByPlaceIdOrderByLatest(placeId, cursor);
		} else {
			Review cursorReview = reviewRepository.findById(cursor).orElse(null);
			if (cursorReview == null) {
				return false;
			}
			return reviewRepository.existsPrevByPlaceIdOrderByScore(
				placeId, 
				cursorReview.getScore(), 
				cursor
			);
		}
	}

	@Override
	public long countTotal() {
		return reviewRepository.countReviewsByPlaceId(placeId);
	}
}
