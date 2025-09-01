package com.server.global.dto.pagination;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiCursorPaginationResponse<T> implements PaginationResponse<T> {

	private int status;
	private boolean hasPrev;
	private boolean hasNext;
	private long total;
	private Long prevCursor;
	private Long nextCursor;
	private LocalDateTime timestamp;
	private List<T> data;
	private String message;
}

