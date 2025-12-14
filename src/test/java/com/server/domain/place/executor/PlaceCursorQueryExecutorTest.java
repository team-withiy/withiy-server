package com.server.domain.place.executor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.server.domain.folder.repository.FolderPlaceRepository;
import com.server.domain.place.entity.Place;
import com.server.domain.place.repository.PlaceRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
@DisplayName("PlaceCursorQueryExecutor 단위 테스트")
class PlaceCursorQueryExecutorTest {

	@Mock
	private PlaceRepository placeRepository;

	@Mock
	private FolderPlaceRepository folderPlaceRepository;

	@Mock
	private Place mockPlace;

	private static final Long FOLDER_ID = 1L;
	private static final Long USER_ID = 100L;
	private static final int LIMIT = 10;

	@Nested
	@DisplayName("특정 폴더 조회 (userId = null)")
	class SpecificFolderTest {

		private PlaceCursorQueryExecutor executor;

		@Nested
		@DisplayName("findNext 테스트")
		class FindNextTest {

			@Test
			@DisplayName("장소 ID 목록을 조회하고 Place 엔티티 반환")
			void findNext_shouldFindPlaceIdsThenFetchPlaces() {
				// given
				executor = new PlaceCursorQueryExecutor(
					placeRepository, folderPlaceRepository, FOLDER_ID, null);
				
				Long cursor = 100L;
				List<Long> placeIds = Arrays.asList(99L, 98L, 97L);
				List<Place> expectedPlaces = Arrays.asList(mockPlace, mockPlace, mockPlace);
				
				when(folderPlaceRepository.findNextPlaceIdsByFolder(
					eq(FOLDER_ID), eq(cursor), any(Pageable.class)))
					.thenReturn(placeIds);
				when(placeRepository.findPlacesByIds(eq(placeIds), any(Sort.class)))
					.thenReturn(expectedPlaces);

				// when
				List<Place> result = executor.findNext(cursor, LIMIT);

				// then
				assertThat(result).isEqualTo(expectedPlaces);
				verify(folderPlaceRepository).findNextPlaceIdsByFolder(
					eq(FOLDER_ID), eq(cursor), any(Pageable.class));
				verify(placeRepository).findPlacesByIds(
					eq(placeIds), eq(Sort.by(Sort.Direction.DESC, "id")));
			}

			@Test
			@DisplayName("장소 ID가 없으면 빈 리스트 반환")
			void findNext_withNoPlaceIds_shouldReturnEmptyList() {
				// given
				executor = new PlaceCursorQueryExecutor(
					placeRepository, folderPlaceRepository, FOLDER_ID, null);
				
				when(folderPlaceRepository.findNextPlaceIdsByFolder(
					eq(FOLDER_ID), eq(null), any(Pageable.class)))
					.thenReturn(Collections.emptyList());

				// when
				List<Place> result = executor.findNext(null, LIMIT);

				// then
				assertThat(result).isEmpty();
				verify(folderPlaceRepository).findNextPlaceIdsByFolder(
					eq(FOLDER_ID), eq(null), any(Pageable.class));
			}
		}

		@Nested
		@DisplayName("findPrev 테스트")
		class FindPrevTest {

			@Test
			@DisplayName("cursor가 null일 때 빈 리스트 반환")
			void findPrev_withNullCursor_shouldReturnEmptyList() {
				// given
				executor = new PlaceCursorQueryExecutor(
					placeRepository, folderPlaceRepository, FOLDER_ID, null);

				// when
				List<Place> result = executor.findPrev(null, LIMIT);

				// then
				assertThat(result).isEmpty();
			}

			@Test
			@DisplayName("장소 ID 목록을 조회하고 ASC 정렬로 Place 반환")
			void findPrev_shouldFindPlaceIdsAndSortAsc() {
				// given
				executor = new PlaceCursorQueryExecutor(
					placeRepository, folderPlaceRepository, FOLDER_ID, null);
				
				Long cursor = 100L;
				List<Long> placeIds = Arrays.asList(101L, 102L, 103L);
				List<Place> expectedPlaces = Arrays.asList(mockPlace, mockPlace, mockPlace);
				
				when(folderPlaceRepository.findPrevPlaceIdsByFolder(
					eq(FOLDER_ID), eq(cursor), any(Pageable.class)))
					.thenReturn(placeIds);
				when(placeRepository.findPlacesByIds(eq(placeIds), any(Sort.class)))
					.thenReturn(expectedPlaces);

				// when
				List<Place> result = executor.findPrev(cursor, LIMIT);

				// then
				assertThat(result).isEqualTo(expectedPlaces);
				verify(folderPlaceRepository).findPrevPlaceIdsByFolder(
					eq(FOLDER_ID), eq(cursor), any(Pageable.class));
				verify(placeRepository).findPlacesByIds(
					eq(placeIds), eq(Sort.by(Sort.Direction.ASC, "id")));
			}
		}

		@Nested
		@DisplayName("existsNext 테스트")
		class ExistsNextTest {

			@Test
			@DisplayName("cursor가 null일 때 false 반환")
			void existsNext_withNullCursor_shouldReturnFalse() {
				// given
				executor = new PlaceCursorQueryExecutor(
					placeRepository, folderPlaceRepository, FOLDER_ID, null);

				// when
				boolean result = executor.existsNext(null);

				// then
				assertThat(result).isFalse();
			}

			@Test
			@DisplayName("다음 장소가 존재하면 true 반환")
			void existsNext_withNextExists_shouldReturnTrue() {
				// given
				executor = new PlaceCursorQueryExecutor(
					placeRepository, folderPlaceRepository, FOLDER_ID, null);
				
				Long cursor = 100L;
				when(folderPlaceRepository.existsNextPlaceByFolder(FOLDER_ID, cursor))
					.thenReturn(true);

				// when
				boolean result = executor.existsNext(cursor);

				// then
				assertThat(result).isTrue();
				verify(folderPlaceRepository).existsNextPlaceByFolder(FOLDER_ID, cursor);
			}
		}

		@Nested
		@DisplayName("existsPrev 테스트")
		class ExistsPrevTest {

			@Test
			@DisplayName("cursor가 null일 때 false 반환")
			void existsPrev_withNullCursor_shouldReturnFalse() {
				// given
				executor = new PlaceCursorQueryExecutor(
					placeRepository, folderPlaceRepository, FOLDER_ID, null);

				// when
				boolean result = executor.existsPrev(null);

				// then
				assertThat(result).isFalse();
			}

			@Test
			@DisplayName("이전 장소가 존재하면 true 반환")
			void existsPrev_withPrevExists_shouldReturnTrue() {
				// given
				executor = new PlaceCursorQueryExecutor(
					placeRepository, folderPlaceRepository, FOLDER_ID, null);
				
				Long cursor = 100L;
				when(folderPlaceRepository.existsPrevPlaceByFolder(FOLDER_ID, cursor))
					.thenReturn(true);

				// when
				boolean result = executor.existsPrev(cursor);

				// then
				assertThat(result).isTrue();
				verify(folderPlaceRepository).existsPrevPlaceByFolder(FOLDER_ID, cursor);
			}
		}

		@Test
		@DisplayName("countTotal - 특정 폴더의 장소 개수 조회")
		void countTotal_shouldCountPlacesInFolder() {
			// given
			executor = new PlaceCursorQueryExecutor(
				placeRepository, folderPlaceRepository, FOLDER_ID, null);
			
			long expectedCount = 25L;
			when(folderPlaceRepository.countPlacesInFolder(FOLDER_ID))
				.thenReturn(expectedCount);

			// when
			long result = executor.countTotal();

			// then
			assertThat(result).isEqualTo(expectedCount);
			verify(folderPlaceRepository).countPlacesInFolder(FOLDER_ID);
		}
	}

	@Nested
	@DisplayName("사용자 전체 폴더 조회 (userId != null)")
	class UserAllFoldersTest {

		private PlaceCursorQueryExecutor executor;

		@Nested
		@DisplayName("findNext 테스트")
		class FindNextTest {

			@Test
			@DisplayName("사용자의 모든 폴더에서 장소 조회")
			void findNext_shouldFindPlacesFromAllUserFolders() {
				// given
				executor = new PlaceCursorQueryExecutor(
					placeRepository, folderPlaceRepository, null, USER_ID);
				
				Long cursor = 100L;
				List<Long> placeIds = Arrays.asList(99L, 98L, 97L);
				List<Place> expectedPlaces = Arrays.asList(mockPlace, mockPlace, mockPlace);
				
				when(folderPlaceRepository.findNextPlaceIdsByUser(
					eq(USER_ID), eq(cursor), any(Pageable.class)))
					.thenReturn(placeIds);
				when(placeRepository.findPlacesByIds(eq(placeIds), any(Sort.class)))
					.thenReturn(expectedPlaces);

				// when
				List<Place> result = executor.findNext(cursor, LIMIT);

				// then
				assertThat(result).isEqualTo(expectedPlaces);
				verify(folderPlaceRepository).findNextPlaceIdsByUser(
					eq(USER_ID), eq(cursor), any(Pageable.class));
				verify(placeRepository).findPlacesByIds(
					eq(placeIds), eq(Sort.by(Sort.Direction.DESC, "id")));
			}
		}

		@Nested
		@DisplayName("findPrev 테스트")
		class FindPrevTest {

			@Test
			@DisplayName("사용자의 모든 폴더에서 이전 장소 조회")
			void findPrev_shouldFindPrevPlacesFromAllUserFolders() {
				// given
				executor = new PlaceCursorQueryExecutor(
					placeRepository, folderPlaceRepository, null, USER_ID);
				
				Long cursor = 100L;
				List<Long> placeIds = Arrays.asList(101L, 102L, 103L);
				List<Place> expectedPlaces = Arrays.asList(mockPlace, mockPlace, mockPlace);
				
				when(folderPlaceRepository.findPrevPlaceIdsByUser(
					eq(USER_ID), eq(cursor), any(Pageable.class)))
					.thenReturn(placeIds);
				when(placeRepository.findPlacesByIds(eq(placeIds), any(Sort.class)))
					.thenReturn(expectedPlaces);

				// when
				List<Place> result = executor.findPrev(cursor, LIMIT);

				// then
				assertThat(result).isEqualTo(expectedPlaces);
				verify(folderPlaceRepository).findPrevPlaceIdsByUser(
					eq(USER_ID), eq(cursor), any(Pageable.class));
				verify(placeRepository).findPlacesByIds(
					eq(placeIds), eq(Sort.by(Sort.Direction.ASC, "id")));
			}
		}

		@Nested
		@DisplayName("existsNext 테스트")
		class ExistsNextTest {

			@Test
			@DisplayName("사용자의 다음 장소 존재 여부 확인")
			void existsNext_shouldCheckNextPlaceForUser() {
				// given
				executor = new PlaceCursorQueryExecutor(
					placeRepository, folderPlaceRepository, null, USER_ID);
				
				Long cursor = 100L;
				when(folderPlaceRepository.existsNextPlaceByUser(USER_ID, cursor))
					.thenReturn(true);

				// when
				boolean result = executor.existsNext(cursor);

				// then
				assertThat(result).isTrue();
				verify(folderPlaceRepository).existsNextPlaceByUser(USER_ID, cursor);
			}
		}

		@Nested
		@DisplayName("existsPrev 테스트")
		class ExistsPrevTest {

			@Test
			@DisplayName("사용자의 이전 장소 존재 여부 확인")
			void existsPrev_shouldCheckPrevPlaceForUser() {
				// given
				executor = new PlaceCursorQueryExecutor(
					placeRepository, folderPlaceRepository, null, USER_ID);
				
				Long cursor = 100L;
				when(folderPlaceRepository.existsPrevPlaceByUser(USER_ID, cursor))
					.thenReturn(true);

				// when
				boolean result = executor.existsPrev(cursor);

				// then
				assertThat(result).isTrue();
				verify(folderPlaceRepository).existsPrevPlaceByUser(USER_ID, cursor);
			}
		}

		@Test
		@DisplayName("countTotal - 사용자의 중복 제거된 장소 개수 조회")
		void countTotal_shouldCountDistinctPlacesByUser() {
			// given
			executor = new PlaceCursorQueryExecutor(
				placeRepository, folderPlaceRepository, null, USER_ID);
			
			long expectedCount = 50L;
			when(folderPlaceRepository.countDistinctPlacesByUser(USER_ID))
				.thenReturn(expectedCount);

			// when
			long result = executor.countTotal();

			// then
			assertThat(result).isEqualTo(expectedCount);
			verify(folderPlaceRepository).countDistinctPlacesByUser(USER_ID);
		}
	}
}
