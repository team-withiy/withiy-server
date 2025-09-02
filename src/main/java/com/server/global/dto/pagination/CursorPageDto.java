package com.server.global.dto.pagination;

import java.util.List;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CursorPageDto<T> {

	private List<T> data;
	private boolean hasPrev;
	private boolean hasNext;
	private long total;
	private Long prevCursor;
	private Long nextCursor;

	public boolean hasPrev() {
		return this.hasPrev;
	}

	public boolean hasNext() {
		return this.hasNext;
	}

	// DTO 변환용 map 메서드
	public <R> CursorPageDto<R> map(Function<T, R> mapper) {
		return CursorPageDto.<R>builder()
			.data(this.data.stream().map(mapper).toList())
			.hasPrev(this.hasPrev)
			.hasNext(this.hasNext)
			.total(this.total)
			.prevCursor(this.prevCursor)
			.nextCursor(this.nextCursor)
			.build();
	}
}
