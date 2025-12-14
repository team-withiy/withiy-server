package com.server.global.pagination.executor;

import java.util.List;

/**
 * 커서 기반 페이지네이션을 위한 쿼리 실행 인터페이스
 *
 * <p>이 인터페이스는 실제 데이터 조회 로직을 추상화합니다.
 * Repository의 쿼리 메서드를 래핑하여 페이징 전략에 제공합니다.
 *
 * @param <T>  엔티티 타입
 * @param <ID> 커서 ID 타입
 */
public interface CursorQueryExecutor<T, ID> {

	/**
	 * 다음 페이지 데이터 조회
	 * cursor보다 뒤에 있는 데이터를 조회합니다.
	 *
	 * @param cursor 현재 커서 위치 (null이면 첫 페이지)
	 * @param limit  조회할 데이터 개수
	 * @return 조회된 데이터 리스트
	 */
	List<T> findNext(ID cursor, int limit);

	/**
	 * 이전 페이지 데이터 조회
	 * cursor보다 앞에 있는 데이터를 조회합니다.
	 *
	 * @param cursor 현재 커서 위치
	 * @param limit  조회할 데이터 개수
	 * @return 조회된 데이터 리스트 (역순)
	 */
	List<T> findPrev(ID cursor, int limit);

	/**
	 * 다음 페이지 존재 여부 확인
	 *
	 * @param cursor 현재 커서 위치
	 * @return 다음 페이지 존재 여부
	 */
	boolean existsNext(ID cursor);

	/**
	 * 이전 페이지 존재 여부 확인
	 *
	 * @param cursor 현재 커서 위치
	 * @return 이전 페이지 존재 여부
	 */
	boolean existsPrev(ID cursor);

	/**
	 * 전체 데이터 개수 조회
	 *
	 * @return 전체 데이터 개수
	 */
	long countTotal();
}
