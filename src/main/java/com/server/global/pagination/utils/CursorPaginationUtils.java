package com.server.global.pagination.utils;

import com.server.global.pagination.dto.CursorPageDto;
import java.util.List;
import java.util.function.Function;

public class CursorPaginationUtils {

	public static <T, ID> CursorPageDto<T, ID> paginate(
		List<T> fetched,        // limit+1 데이터
		int limit,
		boolean isPrev,
		Long cursor,
		Function<T, ID> idExtractor // ID 추출 함수 (ex: Place::getId)
	) {
		boolean hasMore = fetched.size() > limit;

		boolean hasNext;
		boolean hasPrev;
		List<T> data;
		if (isPrev) {
			hasPrev = hasMore;
			hasNext = cursor != null;
			int start = Math.max(0, fetched.size() - limit);
			data = fetched.subList(start, fetched.size());
		} else {
			hasNext = hasMore;
			hasPrev = cursor != null;
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
			.total(data.size())
			.nextCursor(nextCursor)
			.prevCursor(prevCursor)
			.build();
	}
}

