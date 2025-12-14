package com.server.global.pagination.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.server.global.pagination.dto.ApiCursorPaginationRequest;
import com.server.global.pagination.dto.CursorPageDto;
import com.server.global.pagination.executor.CursorQueryExecutor;
import com.server.global.pagination.strategy.CursorPaginationStrategy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CursorPaginationStrategy 단위 테스트")
class CursorPaginationStrategyTest {

	@Mock
	private CursorQueryExecutor<TestEntity, Long> queryExecutor;

	private CursorPaginationStrategy<TestEntity, Long> strategy;

	@BeforeEach
	void setUp() {
		strategy = new CursorPaginationStrategy<>();
	}

	@Nested
	@DisplayName("첫 페이지 조회 테스트")
	class FirstPageTest {

		@Test
		@DisplayName("데이터가 limit보다 많을 때 - hasNext는 true, hasPrev는 false")
		void firstPage_withMoreData_shouldSetHasNextTrue() {
			// given
			int limit = 10;
			long totalCount = 100;
			List<TestEntity> fetchedData = createTestEntities(1, 11); // limit+1개

			ApiCursorPaginationRequest request = ApiCursorPaginationRequest.builder()
				.cursor(null)
				.limit(limit)
				.prev(false)
				.build();

			PaginationContext<TestEntity, Long> context = PaginationContext.<TestEntity, Long>builder()
				.request(request)
				.queryExecutor(queryExecutor)
				.idExtractor(TestEntity::getId)
				.build();

			when(queryExecutor.countTotal()).thenReturn(totalCount);
			when(queryExecutor.findNext(null, limit + 1)).thenReturn(fetchedData);

			// when
			CursorPageDto<TestEntity, Long> result = strategy.executePagination(context);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getData()).hasSize(limit); // limit개만 반환
			assertThat(result.hasNext()).isTrue();
			assertThat(result.hasPrev()).isFalse();
			assertThat(result.getTotal()).isEqualTo(totalCount);
			assertThat(result.getNextCursor()).isEqualTo(10L); // 마지막 데이터의 ID
			assertThat(result.getPrevCursor()).isNull();
		}

		@Test
		@DisplayName("데이터가 limit보다 적을 때 - hasNext는 false")
		void firstPage_withLessData_shouldSetHasNextFalse() {
			// given
			int limit = 10;
			long totalCount = 5;
			List<TestEntity> fetchedData = createTestEntities(1, 5); // limit보다 적음

			ApiCursorPaginationRequest request = ApiCursorPaginationRequest.builder()
				.cursor(null)
				.limit(limit)
				.prev(false)
				.build();

			PaginationContext<TestEntity, Long> context = PaginationContext.<TestEntity, Long>builder()
				.request(request)
				.queryExecutor(queryExecutor)
				.idExtractor(TestEntity::getId)
				.build();

			when(queryExecutor.countTotal()).thenReturn(totalCount);
			when(queryExecutor.findNext(null, limit + 1)).thenReturn(fetchedData);

			// when
			CursorPageDto<TestEntity, Long> result = strategy.executePagination(context);

			// then
			assertThat(result.getData()).hasSize(5);
			assertThat(result.hasNext()).isFalse();
			assertThat(result.hasPrev()).isFalse();
			assertThat(result.getNextCursor()).isNull();
		}

		@Test
		@DisplayName("데이터가 없을 때 - 빈 결과 반환")
		void firstPage_withNoData_shouldReturnEmptyResult() {
			// given
			int limit = 10;
			long totalCount = 0;

			ApiCursorPaginationRequest request = ApiCursorPaginationRequest.builder()
				.cursor(null)
				.limit(limit)
				.prev(false)
				.build();

			PaginationContext<TestEntity, Long> context = PaginationContext.<TestEntity, Long>builder()
				.request(request)
				.queryExecutor(queryExecutor)
				.idExtractor(TestEntity::getId)
				.build();

			when(queryExecutor.countTotal()).thenReturn(totalCount);
			when(queryExecutor.findNext(null, limit + 1)).thenReturn(Collections.emptyList());

			// when
			CursorPageDto<TestEntity, Long> result = strategy.executePagination(context);

			// then
			assertThat(result.getData()).isEmpty();
			assertThat(result.hasNext()).isFalse();
			assertThat(result.hasPrev()).isFalse();
			assertThat(result.getTotal()).isEqualTo(0);
		}
	}

	@Nested
	@DisplayName("다음 페이지 조회 테스트")
	class NextPageTest {

		@Test
		@DisplayName("중간 페이지 조회 - hasNext와 hasPrev 모두 true")
		void nextPage_middlePage_shouldSetBothHasFlags() {
		// given
		int limit = 10;
		long cursor = 50L;
		long totalCount = 100;
		// findNext는 id < cursor를 DESC로 조회 → 49, 48, ..., 39 (limit+1개)
		List<TestEntity> temp = createTestEntities(39, 49);
		List<TestEntity> fetchedData = new ArrayList<>(temp);
		Collections.reverse(fetchedData); // 49~39 DESC

		ApiCursorPaginationRequest request = ApiCursorPaginationRequest.builder()
			.cursor(cursor)
			.limit(limit)
			.prev(false)
			.build();			PaginationContext<TestEntity, Long> context = PaginationContext.<TestEntity, Long>builder()
				.request(request)
				.queryExecutor(queryExecutor)
				.idExtractor(TestEntity::getId)
				.build();

		when(queryExecutor.countTotal()).thenReturn(totalCount);
		when(queryExecutor.findNext(cursor, limit + 1)).thenReturn(fetchedData);
		when(queryExecutor.existsPrev(cursor)).thenReturn(true);

		// when
		CursorPageDto<TestEntity, Long> result = strategy.executePagination(context);		// then
		assertThat(result.getData()).hasSize(limit);
		assertThat(result.hasNext()).isTrue();
		assertThat(result.hasPrev()).isTrue();
		// findNext(50, 11) → [49~39] DESC → subList(0,10) → [49~40]
		assertThat(result.getNextCursor()).isEqualTo(40L); // 마지막 데이터의 ID
		assertThat(result.getPrevCursor()).isEqualTo(49L); // 첫 데이터의 ID
		}

		@Test
		@DisplayName("마지막 페이지 조회 - hasNext는 false, hasPrev는 true")
		void nextPage_lastPage_shouldSetHasNextFalse() {
			// given
			int limit = 10;
			long cursor = 10L;
			long totalCount = 100;
			List<TestEntity> fetchedData = createTestEntities(1, 5); // limit보다 적음

			ApiCursorPaginationRequest request = ApiCursorPaginationRequest.builder()
				.cursor(cursor)
				.limit(limit)
				.prev(false)
				.build();

			PaginationContext<TestEntity, Long> context = PaginationContext.<TestEntity, Long>builder()
				.request(request)
				.queryExecutor(queryExecutor)
				.idExtractor(TestEntity::getId)
				.build();

		when(queryExecutor.countTotal()).thenReturn(totalCount);
		when(queryExecutor.findNext(cursor, limit + 1)).thenReturn(fetchedData);
		when(queryExecutor.existsPrev(cursor)).thenReturn(true);

		// when
		CursorPageDto<TestEntity, Long> result = strategy.executePagination(context);			// then
			assertThat(result.getData()).hasSize(5);
			assertThat(result.hasNext()).isFalse();
			assertThat(result.hasPrev()).isTrue();
			assertThat(result.getNextCursor()).isNull();
		}
	}

	@Nested
	@DisplayName("이전 페이지 조회 테스트")
	class PrevPageTest {

		@Test
		@DisplayName("이전 페이지 조회 - 데이터가 역순으로 정렬됨")
		void prevPage_shouldReverseData() {
			// given
			int limit = 10;
			long cursor = 50L;
			long totalCount = 100;
			// findPrev는 ASC로 조회되므로 51~61
			List<TestEntity> fetchedData = createTestEntities(51, 61); // limit+1개

			ApiCursorPaginationRequest request = ApiCursorPaginationRequest.builder()
				.cursor(cursor)
				.limit(limit)
				.prev(true)
				.build();

			PaginationContext<TestEntity, Long> context = PaginationContext.<TestEntity, Long>builder()
				.request(request)
				.queryExecutor(queryExecutor)
				.idExtractor(TestEntity::getId)
				.build();

		when(queryExecutor.countTotal()).thenReturn(totalCount);
		when(queryExecutor.findPrev(cursor, limit + 1)).thenReturn(fetchedData);
		when(queryExecutor.existsNext(cursor)).thenReturn(true);			// when
			CursorPageDto<TestEntity, Long> result = strategy.executePagination(context);

		// then
		assertThat(result.getData()).hasSize(limit);
		// findPrev(50, 11) → [51~61] (ASC) → reverse → [61~51] → subList(1,11) → [60~51]
		assertThat(result.getData().get(0).getId()).isEqualTo(60L);
		assertThat(result.getData().get(9).getId()).isEqualTo(51L);
		assertThat(result.hasPrev()).isTrue();
		assertThat(result.hasNext()).isTrue();
		}

		@Test
		@DisplayName("첫 페이지로 돌아갈 때 - hasPrev는 false")
		void prevPage_backToFirst_shouldSetHasPrevFalse() {
			// given
			int limit = 10;
			long cursor = 15L;
			long totalCount = 100;
			List<TestEntity> fetchedData = createTestEntities(16, 20); // limit보다 적음

			ApiCursorPaginationRequest request = ApiCursorPaginationRequest.builder()
				.cursor(cursor)
				.limit(limit)
				.prev(true)
				.build();

			PaginationContext<TestEntity, Long> context = PaginationContext.<TestEntity, Long>builder()
				.request(request)
				.queryExecutor(queryExecutor)
				.idExtractor(TestEntity::getId)
				.build();

		when(queryExecutor.countTotal()).thenReturn(totalCount);
		when(queryExecutor.findPrev(cursor, limit + 1)).thenReturn(fetchedData);
		when(queryExecutor.existsNext(cursor)).thenReturn(true);

		// when
		CursorPageDto<TestEntity, Long> result = strategy.executePagination(context);			// then
			assertThat(result.getData()).hasSize(5);
			assertThat(result.hasPrev()).isFalse();
			assertThat(result.hasNext()).isTrue();
		}
	}

	@Nested
	@DisplayName("경계값 테스트")
	class EdgeCaseTest {

		@Test
		@DisplayName("limit이 1일 때")
		void pagination_withLimitOne() {
			// given
			int limit = 1;
			long totalCount = 10;
			List<TestEntity> fetchedData = createTestEntities(1, 2); // limit+1개

			ApiCursorPaginationRequest request = ApiCursorPaginationRequest.builder()
				.cursor(null)
				.limit(limit)
				.prev(false)
				.build();

			PaginationContext<TestEntity, Long> context = PaginationContext.<TestEntity, Long>builder()
				.request(request)
				.queryExecutor(queryExecutor)
				.idExtractor(TestEntity::getId)
				.build();

			when(queryExecutor.countTotal()).thenReturn(totalCount);
			when(queryExecutor.findNext(null, limit + 1)).thenReturn(fetchedData);

			// when
			CursorPageDto<TestEntity, Long> result = strategy.executePagination(context);

			// then
			assertThat(result.getData()).hasSize(1);
			assertThat(result.hasNext()).isTrue();
			assertThat(result.getNextCursor()).isEqualTo(1L);
		}

		@Test
		@DisplayName("limit이 전체 데이터보다 클 때")
		void pagination_limitGreaterThanTotal() {
			// given
			int limit = 100;
			long totalCount = 10;
			List<TestEntity> fetchedData = createTestEntities(1, 10);

			ApiCursorPaginationRequest request = ApiCursorPaginationRequest.builder()
				.cursor(null)
				.limit(limit)
				.prev(false)
				.build();

			PaginationContext<TestEntity, Long> context = PaginationContext.<TestEntity, Long>builder()
				.request(request)
				.queryExecutor(queryExecutor)
				.idExtractor(TestEntity::getId)
				.build();

			when(queryExecutor.countTotal()).thenReturn(totalCount);
			when(queryExecutor.findNext(null, limit + 1)).thenReturn(fetchedData);

			// when
			CursorPageDto<TestEntity, Long> result = strategy.executePagination(context);

			// then
			assertThat(result.getData()).hasSize(10);
			assertThat(result.hasNext()).isFalse();
			assertThat(result.getTotal()).isEqualTo(10);
		}
	}

	// 테스트용 헬퍼 메서드
	private List<TestEntity> createTestEntities(int startId, int endId) {
		List<TestEntity> entities = new java.util.ArrayList<>();
		for (long i = startId; i <= endId; i++) {
			entities.add(new TestEntity(i, "Entity " + i));
		}
		return entities;
	}

	// 테스트용 엔티티 클래스
	private static class TestEntity {
		private final Long id;
		private final String name;

		public TestEntity(Long id, String name) {
			this.id = id;
			this.name = name;
		}

		public Long getId() {
			return id;
		}

		public String getName() {
			return name;
		}
	}
}
