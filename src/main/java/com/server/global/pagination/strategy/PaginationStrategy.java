package com.server.global.pagination.strategy;

import com.server.global.pagination.dto.CursorPageDto;

/**
 * 페이지네이션 전략 인터페이스
 * Strategy 패턴을 사용하여 다양한 페이징 전략을 지원합니다.
 *
 * @param <T>  엔티티 타입
 * @param <ID> 커서 ID 타입
 */
public interface PaginationStrategy<T, ID> {

	/**
	 * 페이지네이션 실행
	 *
	 * @param context 페이지네이션 컨텍스트
	 * @return 페이징 결과
	 */
	CursorPageDto<T, ID> executePagination(PaginationContext<T, ID> context);
}
