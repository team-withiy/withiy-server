package com.server.global.dto.pagination;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiOffsetPaginationRequest implements PaginationRequest {

	private int page;  // 요청할 페이지 번호
	private int size;  // 페이지 크기

	@Override
	public int getLimit() {
		return size;
	}
}

