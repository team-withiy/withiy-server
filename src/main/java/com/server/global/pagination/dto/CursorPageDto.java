package com.server.global.pagination.dto;

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
public class CursorPageDto<T, ID> {

	private List<T> data;
	private boolean hasPrev;
	private boolean hasNext;
	private long total;
	private ID prevCursor;
	private ID nextCursor;

	public boolean hasPrev() {
		return this.hasPrev;
	}

	public boolean hasNext() {
		return this.hasNext;
	}

	// DTO 변환용 map 메서드
	public <R> CursorPageDto<R, ID> map(Function<T, R> mapper) {
		return new CursorPageDto<>(
			this.data.stream().map(mapper).toList(),
			this.hasPrev,
			this.hasNext,
			this.total,
			this.prevCursor,
			this.nextCursor
		);
	}
}
