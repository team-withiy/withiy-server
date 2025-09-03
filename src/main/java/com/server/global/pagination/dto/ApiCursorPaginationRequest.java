package com.server.global.pagination.dto;

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
