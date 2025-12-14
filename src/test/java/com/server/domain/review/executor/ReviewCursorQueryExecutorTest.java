package com.server.domain.review.executor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.server.domain.review.entity.Review;
import com.server.domain.review.entity.ReviewSortType;
import com.server.domain.review.repository.ReviewRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReviewCursorQueryExecutor 단위 테스트")
class ReviewCursorQueryExecutorTest {

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private Review mockReview;

	private static final Long PLACE_ID = 1L;
	private static final int LIMIT = 10;

	@Nested
	@DisplayName("Latest 정렬 (ID 기준)")
	class LatestSortTest {

		private ReviewCursorQueryExecutor executor;

		@BeforeEach
		void setUp() {
			executor = new ReviewCursorQueryExecutor(
				reviewRepository,
				PLACE_ID,
				ReviewSortType.LATEST
			);
		}

		@Test
		@DisplayName("findNext - cursor가 null일 때 첫 페이지 조회")
		void findNext_withNullCursor_shouldCallFirstPageQuery() {
			// given
			List<Review> expectedReviews = Collections.emptyList();
			when(reviewRepository.findByPlaceIdOrderByLatest(eq(PLACE_ID), any(Pageable.class)))
				.thenReturn(expectedReviews);

			// when
			List<Review> result = executor.findNext(null, LIMIT);

			// then
			assertThat(result).isEqualTo(expectedReviews);
			verify(reviewRepository).findByPlaceIdOrderByLatest(eq(PLACE_ID), any(Pageable.class));
		}

		@Test
		@DisplayName("findNext - cursor가 있을 때 다음 페이지 조회")
		void findNext_withCursor_shouldCallNextPageQuery() {
			// given
			Long cursor = 100L;
			List<Review> expectedReviews = Collections.emptyList();
			when(reviewRepository.findNextByPlaceIdOrderByLatest(
				eq(PLACE_ID), eq(cursor), any(Pageable.class)))
				.thenReturn(expectedReviews);

			// when
			List<Review> result = executor.findNext(cursor, LIMIT);

			// then
			assertThat(result).isEqualTo(expectedReviews);
			verify(reviewRepository).findNextByPlaceIdOrderByLatest(
				eq(PLACE_ID), eq(cursor), any(Pageable.class));
		}

		@Test
		@DisplayName("findPrev - cursor가 null일 때 빈 리스트 반환")
		void findPrev_withNullCursor_shouldReturnEmptyList() {
			// when
			List<Review> result = executor.findPrev(null, LIMIT);

			// then
			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("findPrev - cursor가 있을 때 이전 페이지 조회")
		void findPrev_withCursor_shouldCallPrevPageQuery() {
			// given
			Long cursor = 100L;
			List<Review> expectedReviews = Collections.emptyList();
			when(reviewRepository.findPrevByPlaceIdOrderByLatest(
				eq(PLACE_ID), eq(cursor), any(Pageable.class)))
				.thenReturn(expectedReviews);

			// when
			List<Review> result = executor.findPrev(cursor, LIMIT);

			// then
			assertThat(result).isEqualTo(expectedReviews);
			verify(reviewRepository).findPrevByPlaceIdOrderByLatest(
				eq(PLACE_ID), eq(cursor), any(Pageable.class));
		}

		@Test
		@DisplayName("existsNext - cursor가 null일 때 false 반환")
		void existsNext_withNullCursor_shouldReturnFalse() {
			// when
			boolean result = executor.existsNext(null);

			// then
			assertThat(result).isFalse();
		}

		@Test
		@DisplayName("existsNext - cursor가 있을 때 repository 호출")
		void existsNext_withCursor_shouldCallRepository() {
			// given
			Long cursor = 100L;
			when(reviewRepository.existsNextByPlaceIdOrderByLatest(PLACE_ID, cursor))
				.thenReturn(true);

			// when
			boolean result = executor.existsNext(cursor);

			// then
			assertThat(result).isTrue();
			verify(reviewRepository).existsNextByPlaceIdOrderByLatest(PLACE_ID, cursor);
		}

		@Test
		@DisplayName("existsPrev - cursor가 null일 때 false 반환")
		void existsPrev_withNullCursor_shouldReturnFalse() {
			// when
			boolean result = executor.existsPrev(null);

			// then
			assertThat(result).isFalse();
		}

		@Test
		@DisplayName("existsPrev - cursor가 있을 때 repository 호출")
		void existsPrev_withCursor_shouldCallRepository() {
			// given
			Long cursor = 100L;
			when(reviewRepository.existsPrevByPlaceIdOrderByLatest(PLACE_ID, cursor))
				.thenReturn(true);

			// when
			boolean result = executor.existsPrev(cursor);

			// then
			assertThat(result).isTrue();
			verify(reviewRepository).existsPrevByPlaceIdOrderByLatest(PLACE_ID, cursor);
		}

		@Test
		@DisplayName("countTotal - repository 호출")
		void countTotal_shouldCallRepository() {
			// given
			long expectedCount = 100L;
			when(reviewRepository.countReviewsByPlaceId(PLACE_ID))
				.thenReturn(expectedCount);

			// when
			long result = executor.countTotal();

			// then
			assertThat(result).isEqualTo(expectedCount);
			verify(reviewRepository).countReviewsByPlaceId(PLACE_ID);
		}
	}

	@Nested
	@DisplayName("Score 정렬 (평점 + ID 기준)")
	class ScoreSortTest {

		private ReviewCursorQueryExecutor executor;

		@BeforeEach
		void setUp() {
			executor = new ReviewCursorQueryExecutor(
				reviewRepository,
				PLACE_ID,
				ReviewSortType.SCORE
			);
		}

		@Test
		@DisplayName("findNext - cursor가 null일 때 첫 페이지 조회")
		void findNext_withNullCursor_shouldCallFirstPageQuery() {
			// given
			List<Review> expectedReviews = Collections.emptyList();
			when(reviewRepository.findByPlaceIdOrderByScore(eq(PLACE_ID), any(Pageable.class)))
				.thenReturn(expectedReviews);

			// when
			List<Review> result = executor.findNext(null, LIMIT);

			// then
			assertThat(result).isEqualTo(expectedReviews);
			verify(reviewRepository).findByPlaceIdOrderByScore(eq(PLACE_ID), any(Pageable.class));
		}

		@Test
		@DisplayName("findNext - cursor가 있고 리뷰가 존재할 때 다음 페이지 조회")
		void findNext_withCursorAndReviewExists_shouldCallNextPageQuery() {
			// given
			Long cursor = 100L;
			Long score = 5L;
			when(mockReview.getScore()).thenReturn(score);
			when(reviewRepository.findById(cursor)).thenReturn(Optional.of(mockReview));

			List<Review> expectedReviews = Collections.emptyList();
			when(reviewRepository.findNextByPlaceIdOrderByScore(
				eq(PLACE_ID), eq(score), eq(cursor), any(Pageable.class)))
				.thenReturn(expectedReviews);

			// when
			List<Review> result = executor.findNext(cursor, LIMIT);

			// then
			assertThat(result).isEqualTo(expectedReviews);
			verify(reviewRepository).findById(cursor);
			verify(reviewRepository).findNextByPlaceIdOrderByScore(
				eq(PLACE_ID), eq(score), eq(cursor), any(Pageable.class));
		}

		@Test
		@DisplayName("findNext - cursor의 리뷰가 없을 때 빈 리스트 반환")
		void findNext_withCursorButReviewNotExists_shouldReturnEmptyList() {
			// given
			Long cursor = 100L;
			when(reviewRepository.findById(cursor)).thenReturn(Optional.empty());

			// when
			List<Review> result = executor.findNext(cursor, LIMIT);

			// then
			assertThat(result).isEmpty();
			verify(reviewRepository).findById(cursor);
		}

		@Test
		@DisplayName("findPrev - cursor가 있고 리뷰가 존재할 때 이전 페이지 조회")
		void findPrev_withCursorAndReviewExists_shouldCallPrevPageQuery() {
			// given
			Long cursor = 100L;
			Long score = 5L;
			when(mockReview.getScore()).thenReturn(score);
			when(reviewRepository.findById(cursor)).thenReturn(Optional.of(mockReview));

			List<Review> expectedReviews = Collections.emptyList();
			when(reviewRepository.findPrevByPlaceIdOrderByScore(
				eq(PLACE_ID), eq(score), eq(cursor), any(Pageable.class)))
				.thenReturn(expectedReviews);

			// when
			List<Review> result = executor.findPrev(cursor, LIMIT);

			// then
			assertThat(result).isEqualTo(expectedReviews);
			verify(reviewRepository).findPrevByPlaceIdOrderByScore(
				eq(PLACE_ID), eq(score), eq(cursor), any(Pageable.class));
		}

		@Test
		@DisplayName("existsNext - cursor의 리뷰가 없을 때 false 반환")
		void existsNext_withReviewNotExists_shouldReturnFalse() {
			// given
			Long cursor = 100L;
			when(reviewRepository.findById(cursor)).thenReturn(Optional.empty());

			// when
			boolean result = executor.existsNext(cursor);

			// then
			assertThat(result).isFalse();
		}

		@Test
		@DisplayName("existsNext - cursor의 리뷰가 있을 때 repository 호출")
		void existsNext_withReviewExists_shouldCallRepository() {
			// given
			Long cursor = 100L;
			Long score = 5L;
			when(mockReview.getScore()).thenReturn(score);
			when(reviewRepository.findById(cursor)).thenReturn(Optional.of(mockReview));
			when(reviewRepository.existsNextByPlaceIdOrderByScore(PLACE_ID, score, cursor))
				.thenReturn(true);

			// when
			boolean result = executor.existsNext(cursor);

			// then
			assertThat(result).isTrue();
			verify(reviewRepository).existsNextByPlaceIdOrderByScore(PLACE_ID, score, cursor);
		}

		@Test
		@DisplayName("existsPrev - cursor의 리뷰가 있을 때 repository 호출")
		void existsPrev_withReviewExists_shouldCallRepository() {
			// given
			Long cursor = 100L;
			Long score = 5L;
			when(mockReview.getScore()).thenReturn(score);
			when(reviewRepository.findById(cursor)).thenReturn(Optional.of(mockReview));
			when(reviewRepository.existsPrevByPlaceIdOrderByScore(PLACE_ID, score, cursor))
				.thenReturn(true);

			// when
			boolean result = executor.existsPrev(cursor);

			// then
			assertThat(result).isTrue();
			verify(reviewRepository).existsPrevByPlaceIdOrderByScore(PLACE_ID, score, cursor);
		}
	}
}
