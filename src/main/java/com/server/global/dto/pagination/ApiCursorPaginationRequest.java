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

	private Long cursor;
	private int limit;
	private Boolean prev;
}
