package com.server.global.pagination.strategy;

import com.server.global.pagination.dto.ApiCursorPaginationRequest;
import com.server.global.pagination.executor.CursorQueryExecutor;
import java.util.function.Function;
import lombok.Builder;
import lombok.Getter;

/**
 * 페이지네이션 실행에 필요한 컨텍스트 정보
 * 
 * <p>페이징 전략 실행 시 필요한 모든 정보를 담고 있습니다:
 * <ul>
 *   <li>요청 정보 (cursor, limit, prev)</li>
 *   <li>쿼리 실행자 (Repository 래퍼)</li>
 *   <li>ID 추출 함수 (엔티티에서 커서 추출)</li>
 * </ul>
 *
 * @param <T>  엔티티 타입
 * @param <ID> 커서 ID 타입
 */
@Getter
@Builder
public class PaginationContext<T, ID> {

	/**
	 * 페이지네이션 요청 정보
	 */
	private ApiCursorPaginationRequest request;

	/**
	 * 데이터 조회를 위한 쿼리 실행자
	 */
	private CursorQueryExecutor<T, ID> queryExecutor;

	/**
	 * 엔티티에서 커서 ID를 추출하는 함수
	 * 예: Photo::getId, Review::getId
	 */
	private Function<T, ID> idExtractor;
}
