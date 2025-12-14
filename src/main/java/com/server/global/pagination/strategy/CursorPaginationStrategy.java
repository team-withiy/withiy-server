package com.server.global.pagination.strategy;

import com.server.global.pagination.dto.ApiCursorPaginationRequest;
import com.server.global.pagination.dto.CursorPageDto;
import com.server.global.pagination.executor.CursorQueryExecutor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 커서 기반 페이지네이션 전략 구현체 ID를 커서로 사용하여 다음/이전 페이지를 조회
 *
 * <p><b>동작 방식:</b>
 * <ul>
 *   <li>다음 페이지: cursor보다 작은 ID를 내림차순으로 조회</li>
 *   <li>이전 페이지: cursor보다 큰 ID를 오름차순으로 조회 후 역순 정렬</li>
 *   <li>첫 페이지: cursor가 null이면 최신 데이터부터 조회</li>
 * </ul>
 *
 * <p><b>성능 최적화:</b>
 * <ul>
 *   <li>limit+1개를 조회하여 다음 페이지 존재 여부를 효율적으로 판단</li>
 *   <li>ID 인덱스를 활용하여 빠른 조회 가능</li>
 *   <li>OFFSET을 사용하지 않아 대용량 데이터에서도 성능 유지</li>
 * </ul>
 */
@Slf4j
@Component
public class CursorPaginationStrategy<T, ID> implements PaginationStrategy<T, ID> {

	@Override
	public CursorPageDto<T, ID> executePagination(PaginationContext<T, ID> context) {
		ApiCursorPaginationRequest request = context.getRequest();
		CursorQueryExecutor<T, ID> executor = context.getQueryExecutor();
		Function<T, ID> idExtractor = context.getIdExtractor();

		int limit = request.getLimit();
		@SuppressWarnings("unchecked")
		ID cursor = (ID) request.getCursor(); // Long -> ID 캐스팅
		boolean isPrev = Boolean.TRUE.equals(request.getPrev());

		// 1. 전체 개수 조회
		long total = executor.countTotal();

		// 2. 커서가 null이면 첫 페이지
		if (cursor == null) {
			return handleFirstPage(executor, idExtractor, limit, total);
		}

		// 3. 이전/다음 페이지 분기 처리
		if (isPrev) {
			return handlePrevPage(executor, idExtractor, cursor, limit, total);
		} else {
			return handleNextPage(executor, idExtractor, cursor, limit, total);
		}
	}

	/**
	 * 첫 페이지 처리 (cursor = null)
	 *
	 * @param executor    쿼리 실행자
	 * @param idExtractor ID 추출 함수
	 * @param limit       페이지 크기
	 * @param total       전체 데이터 개수
	 * @return 첫 페이지 결과
	 */
	private CursorPageDto<T, ID> handleFirstPage(
		CursorQueryExecutor<T, ID> executor,
		Function<T, ID> idExtractor,
		int limit,
		long total
	) {
		// limit+1개 조회 (다음 페이지 존재 여부 확인용)
		List<T> fetched = executor.findNext(null, limit + 1);
		boolean hasNext = fetched.size() > limit;

		// 실제 데이터는 limit개만 사용
		List<T> data = hasNext ? fetched.subList(0, limit) : fetched;
		ID nextCursor = hasNext && !data.isEmpty()
			? idExtractor.apply(data.get(data.size() - 1))
			: null;

		return CursorPageDto.<T, ID>builder()
			.data(data)
			.hasNext(hasNext)
			.hasPrev(false)
			.total(total)
			.nextCursor(nextCursor)
			.prevCursor(null)
			.build();
	}

	/**
	 * 다음 페이지 처리
	 *
	 * @param executor    쿼리 실행자
	 * @param idExtractor ID 추출 함수
	 * @param cursor      현재 커서
	 * @param limit       페이지 크기
	 * @param total       전체 데이터 개수
	 * @return 다음 페이지 결과
	 */
	private CursorPageDto<T, ID> handleNextPage(
		CursorQueryExecutor<T, ID> executor,
		Function<T, ID> idExtractor,
		ID cursor,
		int limit,
		long total
	) {
		// limit+1개 조회
		List<T> fetched = executor.findNext(cursor, limit + 1);
		boolean hasMore = fetched.size() > limit;
		boolean hasNext = hasMore;
		boolean hasPrev = executor.existsPrev(cursor);

		// 실제 데이터는 limit개만 사용
		List<T> data = hasMore ? fetched.subList(0, limit) : fetched;
		ID nextCursor = hasNext && !data.isEmpty()
			? idExtractor.apply(data.get(data.size() - 1))
			: null;
		ID prevCursor = hasPrev && !data.isEmpty()
			? idExtractor.apply(data.get(0))
			: null;

		return CursorPageDto.<T, ID>builder()
			.data(data)
			.hasNext(hasNext)
			.hasPrev(hasPrev)
			.total(total)
			.nextCursor(nextCursor)
			.prevCursor(prevCursor)
			.build();
	}

	/**
	 * 이전 페이지 처리
	 *
	 * @param executor    쿼리 실행자
	 * @param idExtractor ID 추출 함수
	 * @param cursor      현재 커서
	 * @param limit       페이지 크기
	 * @param total       전체 데이터 개수
	 * @return 이전 페이지 결과
	 */
	private CursorPageDto<T, ID> handlePrevPage(
		CursorQueryExecutor<T, ID> executor,
		Function<T, ID> idExtractor,
		ID cursor,
		int limit,
		long total
	) {
		// limit+1개 조회 (오름차순)
		List<T> fetched = executor.findPrev(cursor, limit + 1);
		List<T> reversed = new ArrayList<>(fetched); // 불변 리스트 방지
		Collections.reverse(reversed); // 오름차순 -> 내림차순
		fetched = reversed;

		boolean hasMore = fetched.size() > limit;
		boolean hasPrev = hasMore;
		boolean hasNext = executor.existsNext(cursor);

		// 뒤에서 limit개만 사용
		int start = Math.max(0, fetched.size() - limit);
		List<T> data = fetched.subList(start, fetched.size());

		ID prevCursor = hasPrev && !data.isEmpty()
			? idExtractor.apply(data.get(0))
			: null;
		ID nextCursor = hasNext && !data.isEmpty()
			? idExtractor.apply(data.get(data.size() - 1))
			: null;

		return CursorPageDto.<T, ID>builder()
			.data(data)
			.hasNext(hasNext)
			.hasPrev(hasPrev)
			.total(total)
			.nextCursor(nextCursor)
			.prevCursor(prevCursor)
			.build();
	}
}
