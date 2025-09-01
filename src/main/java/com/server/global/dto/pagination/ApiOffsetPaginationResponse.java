package com.server.global.dto.pagination;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiOffsetPaginationResponse<T> implements PaginationResponse<T> {

	private int status;
	private int totalPages;
	private long total;
	private int currentPage;
	private int size;
	private List<T> data;
	private String message;
}
