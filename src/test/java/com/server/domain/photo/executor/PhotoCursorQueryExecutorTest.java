package com.server.domain.photo.executor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.server.domain.photo.entity.Photo;
import com.server.domain.photo.entity.PhotoType;
import com.server.domain.photo.repository.PhotoRepository;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@DisplayName("PhotoCursorQueryExecutor 단위 테스트")
class PhotoCursorQueryExecutorTest {

	@Mock
	private PhotoRepository photoRepository;

	private PhotoCursorQueryExecutor executor;

	private static final Long PLACE_ID = 1L;
	private static final PhotoType PHOTO_TYPE = PhotoType.PUBLIC;
	private static final int LIMIT = 10;

	@BeforeEach
	void setUp() {
		executor = new PhotoCursorQueryExecutor(photoRepository, PLACE_ID, PHOTO_TYPE);
	}

	@Nested
	@DisplayName("findNext 테스트")
	class FindNextTest {

		@Test
		@DisplayName("cursor가 null일 때 첫 페이지 조회")
		void findNext_withNullCursor_shouldCallFirstPageQuery() {
			// given
			List<Photo> expectedPhotos = Collections.emptyList();
			when(photoRepository.findPhotosByPlaceIdAndType(
				eq(PLACE_ID), eq(PHOTO_TYPE), any(Pageable.class)))
				.thenReturn(expectedPhotos);

			// when
			List<Photo> result = executor.findNext(null, LIMIT);

			// then
			assertThat(result).isEqualTo(expectedPhotos);
			verify(photoRepository).findPhotosByPlaceIdAndType(
				eq(PLACE_ID), eq(PHOTO_TYPE), any(Pageable.class));
		}

		@Test
		@DisplayName("cursor가 있을 때 다음 페이지 조회")
		void findNext_withCursor_shouldCallNextPageQuery() {
			// given
			Long cursor = 100L;
			List<Photo> expectedPhotos = Collections.emptyList();
			when(photoRepository.findNextPhotosByPlaceIdAndType(
				eq(PLACE_ID), eq(PHOTO_TYPE), eq(cursor), any(Pageable.class)))
				.thenReturn(expectedPhotos);

			// when
			List<Photo> result = executor.findNext(cursor, LIMIT);

			// then
			assertThat(result).isEqualTo(expectedPhotos);
			verify(photoRepository).findNextPhotosByPlaceIdAndType(
				eq(PLACE_ID), eq(PHOTO_TYPE), eq(cursor), any(Pageable.class));
		}
	}

	@Nested
	@DisplayName("findPrev 테스트")
	class FindPrevTest {

		@Test
		@DisplayName("cursor가 null일 때 빈 리스트 반환")
		void findPrev_withNullCursor_shouldReturnEmptyList() {
			// when
			List<Photo> result = executor.findPrev(null, LIMIT);

			// then
			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("cursor가 있을 때 이전 페이지 조회")
		void findPrev_withCursor_shouldCallPrevPageQuery() {
			// given
			Long cursor = 100L;
			List<Photo> expectedPhotos = Collections.emptyList();
			when(photoRepository.findPrevPhotosByPlaceIdAndType(
				eq(PLACE_ID), eq(PHOTO_TYPE), eq(cursor), any(Pageable.class)))
				.thenReturn(expectedPhotos);

			// when
			List<Photo> result = executor.findPrev(cursor, LIMIT);

			// then
			assertThat(result).isEqualTo(expectedPhotos);
			verify(photoRepository).findPrevPhotosByPlaceIdAndType(
				eq(PLACE_ID), eq(PHOTO_TYPE), eq(cursor), any(Pageable.class));
		}
	}

	@Nested
	@DisplayName("existsNext 테스트")
	class ExistsNextTest {

		@Test
		@DisplayName("cursor가 null일 때 false 반환")
		void existsNext_withNullCursor_shouldReturnFalse() {
			// when
			boolean result = executor.existsNext(null);

			// then
			assertThat(result).isFalse();
		}

		@Test
		@DisplayName("cursor가 있고 다음 페이지가 존재할 때 true 반환")
		void existsNext_withCursorAndNextExists_shouldReturnTrue() {
			// given
			Long cursor = 100L;
			when(photoRepository.existsNextPhotoByPlaceIdAndType(PLACE_ID, PHOTO_TYPE, cursor))
				.thenReturn(true);

			// when
			boolean result = executor.existsNext(cursor);

			// then
			assertThat(result).isTrue();
			verify(photoRepository).existsNextPhotoByPlaceIdAndType(PLACE_ID, PHOTO_TYPE, cursor);
		}

		@Test
		@DisplayName("cursor가 있고 다음 페이지가 없을 때 false 반환")
		void existsNext_withCursorAndNextNotExists_shouldReturnFalse() {
			// given
			Long cursor = 100L;
			when(photoRepository.existsNextPhotoByPlaceIdAndType(PLACE_ID, PHOTO_TYPE, cursor))
				.thenReturn(false);

			// when
			boolean result = executor.existsNext(cursor);

			// then
			assertThat(result).isFalse();
			verify(photoRepository).existsNextPhotoByPlaceIdAndType(PLACE_ID, PHOTO_TYPE, cursor);
		}
	}

	@Nested
	@DisplayName("existsPrev 테스트")
	class ExistsPrevTest {

		@Test
		@DisplayName("cursor가 null일 때 false 반환")
		void existsPrev_withNullCursor_shouldReturnFalse() {
			// when
			boolean result = executor.existsPrev(null);

			// then
			assertThat(result).isFalse();
		}

		@Test
		@DisplayName("cursor가 있고 이전 페이지가 존재할 때 true 반환")
		void existsPrev_withCursorAndPrevExists_shouldReturnTrue() {
			// given
			Long cursor = 100L;
			when(photoRepository.existsPrevPhotoByPlaceIdAndType(PLACE_ID, PHOTO_TYPE, cursor))
				.thenReturn(true);

			// when
			boolean result = executor.existsPrev(cursor);

			// then
			assertThat(result).isTrue();
			verify(photoRepository).existsPrevPhotoByPlaceIdAndType(PLACE_ID, PHOTO_TYPE, cursor);
		}

		@Test
		@DisplayName("cursor가 있고 이전 페이지가 없을 때 false 반환")
		void existsPrev_withCursorAndPrevNotExists_shouldReturnFalse() {
			// given
			Long cursor = 100L;
			when(photoRepository.existsPrevPhotoByPlaceIdAndType(PLACE_ID, PHOTO_TYPE, cursor))
				.thenReturn(false);

			// when
			boolean result = executor.existsPrev(cursor);

			// then
			assertThat(result).isFalse();
			verify(photoRepository).existsPrevPhotoByPlaceIdAndType(PLACE_ID, PHOTO_TYPE, cursor);
		}
	}

	@Nested
	@DisplayName("countTotal 테스트")
	class CountTotalTest {

		@Test
		@DisplayName("전체 개수 조회")
		void countTotal_shouldCallRepository() {
			// given
			long expectedCount = 42L;
			when(photoRepository.countPhotosByPlaceIdAndType(PLACE_ID, PHOTO_TYPE))
				.thenReturn(expectedCount);

			// when
			long result = executor.countTotal();

			// then
			assertThat(result).isEqualTo(expectedCount);
			verify(photoRepository).countPhotosByPlaceIdAndType(PLACE_ID, PHOTO_TYPE);
		}

		@Test
		@DisplayName("사진이 없을 때 0 반환")
		void countTotal_withNoPhotos_shouldReturnZero() {
			// given
			when(photoRepository.countPhotosByPlaceIdAndType(PLACE_ID, PHOTO_TYPE))
				.thenReturn(0L);

			// when
			long result = executor.countTotal();

			// then
			assertThat(result).isZero();
			verify(photoRepository).countPhotosByPlaceIdAndType(PLACE_ID, PHOTO_TYPE);
		}
	}

	@Nested
	@DisplayName("PhotoType별 동작 테스트")
	class PhotoTypeTest {

		@Test
		@DisplayName("REVIEW 타입 사진 조회")
		void findNext_withReviewType_shouldUseReviewType() {
			// given
			PhotoCursorQueryExecutor reviewExecutor = new PhotoCursorQueryExecutor(
				photoRepository, PLACE_ID, PhotoType.PUBLIC);
			List<Photo> expectedPhotos = Collections.emptyList();
			when(photoRepository.findPhotosByPlaceIdAndType(
				eq(PLACE_ID), eq(PhotoType.PUBLIC), any(Pageable.class)))
				.thenReturn(expectedPhotos);

			// when
			List<Photo> result = reviewExecutor.findNext(null, LIMIT);

			// then
			assertThat(result).isEqualTo(expectedPhotos);
			verify(photoRepository).findPhotosByPlaceIdAndType(
				eq(PLACE_ID), eq(PhotoType.PUBLIC), any(Pageable.class));
		}

		@Test
		@DisplayName("PLACE 타입 사진 조회")
		void findNext_withPlaceType_shouldUsePlaceType() {
			// given
			PhotoCursorQueryExecutor placeExecutor = new PhotoCursorQueryExecutor(
				photoRepository, PLACE_ID, PhotoType.PRIVATE);
			List<Photo> expectedPhotos = Collections.emptyList();
			when(photoRepository.findPhotosByPlaceIdAndType(
				eq(PLACE_ID), eq(PhotoType.PRIVATE), any(Pageable.class)))
				.thenReturn(expectedPhotos);

			// when
			List<Photo> result = placeExecutor.findNext(null, LIMIT);

			// then
			assertThat(result).isEqualTo(expectedPhotos);
			verify(photoRepository).findPhotosByPlaceIdAndType(
				eq(PLACE_ID), eq(PhotoType.PRIVATE), any(Pageable.class));
		}
	}
}
