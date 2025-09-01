package com.server.global.dto.pagination;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiCursorPaginationRequest implements PaginationRequest {

	private Long where;         // 커서 (id)
	private int limit;          // 몇 개 가져올지
	private Boolean prev;       // 이전 방향?
	private Boolean includeCursor; // 커서 포함 여부
}
