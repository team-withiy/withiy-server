package com.server.global.pagination.utils;

import com.server.global.pagination.dto.CursorPageDto;
import java.util.List;
import java.util.function.Function;

public class CursorPaginationUtils {

	/**
	 * 커서 기반 페이지네이션 처리 유틸리티 메서드
	 *
	 * @param total       총 데이터 수
	 * @param fetched     실제로 가져온 데이터 (limit + 1 개)
	 * @param limit       한 번에 가져올 데이터 수
	 * @param isPrev      이전 페이지 요청 여부
	 * @param cursor      현재 커서 위치
	 * @param hasPrev     이전 페이지 존재 여부
	 * @param hasNext     다음 페이지 존재 여부
	 * @param idExtractor ID 추출 함수 (ex: Place::getId)
	 * @param <T>         데이터 타입
	 * @param <ID>        ID 타입
	 * @return CursorPageDto<T, ID> 커서 페이지 DTO
	 */
	public static <T, ID> CursorPageDto<T, ID> paginate(
		long total,
		List<T> fetched,        // limit+1 데이터
		int limit,
		boolean isPrev,
		Long cursor,
		boolean hasPrev,
		boolean hasNext,
		Function<T, ID> idExtractor // ID 추출 함수 (ex: Place::getId)
	) {
		List<T> data;
		if (isPrev) {
			int start = Math.max(0, fetched.size() - limit);
			data = fetched.subList(start, fetched.size());
		} else {
			int end = Math.min(fetched.size(), limit);
			data = fetched.subList(0, end);
		}

		// 커서 설정
		ID nextCursor =
			(!data.isEmpty() && hasNext) ? idExtractor.apply(data.get(data.size() - 1)) : null;
		ID prevCursor = (!data.isEmpty() && hasPrev) ? idExtractor.apply(data.get(0)) : null;

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

