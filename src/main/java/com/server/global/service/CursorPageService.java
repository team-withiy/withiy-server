package com.server.global.service;

import com.server.global.pagination.dto.ApiCursorPaginationRequest;
import com.server.global.pagination.dto.CursorPageDto;
import java.util.Collections;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public abstract class CursorPageService<T, ID> {

	/**
	 * Next 모드에서 데이터 조회
	 *
	 * @param cursor   기준 커서 (없으면 첫 페이지)
	 * @param pageable limit+1 사이즈
	 */
	protected abstract List<T> findNext(Long parentId, ID cursor, Pageable pageable);

	/**
	 * Prev 모드에서 데이터 조회
	 *
	 * @param cursor   기준 커서 (없으면 첫 페이지)
	 * @param pageable limit+1 사이즈
	 */
	protected abstract List<T> findPrev(Long parentId, ID cursor, Pageable pageable);

	/**
	 * 엔티티에서 커서로 사용할 ID 추출
	 */
	protected abstract ID extractId(T entity);

	/**
	 * 커서 기반 페이징 처리
	 */
	public CursorPageDto<T, ID> getPage(Long parentId, ApiCursorPaginationRequest request) {
		int limit = request.getLimit();
		Pageable pageable = PageRequest.of(0, limit + 1);

		List<T> fetched;
		boolean isPrev = Boolean.TRUE.equals(request.getPrev());

		if (isPrev) {
			fetched = findPrev(parentId, (ID) request.getCursor(), pageable);
			Collections.reverse(fetched);
		} else {
			fetched = findNext(parentId, (ID) request.getCursor(), pageable);
		}

		boolean hasMore = fetched.size() > limit;

		boolean hasNext;
		boolean hasPrev;

		if (isPrev) {
			hasPrev = hasMore;
			hasNext = request.getCursor() != null;
		} else {
			hasNext = hasMore;
			hasPrev = request.getCursor() != null;
		}

		List<T> data = hasMore ? fetched.subList(0, limit) : fetched;

		ID nextCursor = (!data.isEmpty() && hasNext) ? extractId(data.get(data.size() - 1)) : null;
		ID prevCursor = (!data.isEmpty() && hasPrev) ? extractId(data.get(0)) : null;

		return CursorPageDto.<T, ID>builder()
			.data(data)
			.hasNext(hasNext)
			.hasPrev(hasPrev)
			.total(data.size())
			.nextCursor(nextCursor)
			.prevCursor(prevCursor)
			.build();
	}
}
