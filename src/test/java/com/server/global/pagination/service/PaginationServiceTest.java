package com.server.global.pagination.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.server.global.pagination.dto.ApiCursorPaginationRequest;
import com.server.global.pagination.dto.CursorPageDto;
import com.server.global.pagination.executor.CursorQueryExecutor;
import com.server.global.pagination.strategy.PaginationContext;
import com.server.global.pagination.strategy.PaginationStrategy;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaginationService 단위 테스트")
class PaginationServiceTest {

	@Mock
	private PaginationStrategy<Object, Object> paginationStrategy;

	@Mock
	private CursorQueryExecutor<TestEntity, Long> queryExecutor;

	private PaginationService paginationService;

	@BeforeEach
	void setUp() {
		paginationService = new PaginationService(paginationStrategy);
	}

	@Nested
	@DisplayName("paginate 메서드 테스트")
	class PaginateTest {

		@Test
		@DisplayName("Strategy에 컨텍스트를 위임하고 결과 반환")
		void paginate_shouldDelegateToStrategyAndReturnResult() {
			// given
			ApiCursorPaginationRequest request = ApiCursorPaginationRequest.builder()
				.limit(10)
				.cursor(null)
				.prev(false)
				.build();

			Function<TestEntity, Long> idExtractor = TestEntity::getId;

			PaginationContext<TestEntity, Long> context = PaginationContext.<TestEntity, Long>builder()
				.request(request)
				.queryExecutor(queryExecutor)
				.idExtractor(idExtractor)
				.build();

			List<TestEntity> entities = Arrays.asList(
				new TestEntity(1L),
				new TestEntity(2L),
				new TestEntity(3L)
			);

		CursorPageDto<TestEntity, Long> expectedResult = CursorPageDto.<TestEntity, Long>builder()
			.data(entities)
			.nextCursor(3L)
			.hasNext(false)
			.total(3L)
			.build();

		when(paginationStrategy.executePagination(any())).thenAnswer(invocation -> expectedResult);

		// when
		@SuppressWarnings("unchecked")
		CursorPageDto<TestEntity, Long> result = paginationService.paginate(context);			// then
			assertThat(result).isEqualTo(expectedResult);
			assertThat(result.getData()).hasSize(3);
			assertThat(result.getNextCursor()).isEqualTo(3L);
			assertThat(result.isHasNext()).isFalse();
			verify(paginationStrategy).executePagination(any());
		}

		@Test
		@DisplayName("빈 결과 처리")
		void paginate_shouldHandleEmptyResult() {
			// given
			ApiCursorPaginationRequest request = ApiCursorPaginationRequest.builder()
				.limit(10)
				.cursor(null)
				.prev(false)
				.build();

			Function<TestEntity, Long> idExtractor = TestEntity::getId;

			PaginationContext<TestEntity, Long> context = PaginationContext.<TestEntity, Long>builder()
				.request(request)
				.queryExecutor(queryExecutor)
				.idExtractor(idExtractor)
				.build();

		CursorPageDto<TestEntity, Long> expectedResult = CursorPageDto.<TestEntity, Long>builder()
			.data(List.of())
			.nextCursor(null)
			.hasNext(false)
			.total(0L)
			.build();

		when(paginationStrategy.executePagination(any())).thenAnswer(invocation -> expectedResult);

		// when
		@SuppressWarnings("unchecked")
		CursorPageDto<TestEntity, Long> result = paginationService.paginate(context);			// then
			assertThat(result.getData()).isEmpty();
			assertThat(result.getNextCursor()).isNull();
			assertThat(result.isHasNext()).isFalse();
			assertThat(result.getTotal()).isZero();
			verify(paginationStrategy).executePagination(any());
		}
	}



	// 테스트용 엔티티 클래스
	static class TestEntity {
		private final Long id;

		TestEntity(Long id) {
			this.id = id;
		}

		Long getId() {
			return id;
		}
	}
}
