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
	private String message;
	private List<T> data;
	private LocalDateTime timestamp;
	private boolean hasPrev;
	private boolean hasNext;
	private long total;
	private Long prevCursor;
	private Long nextCursor;

	public static <T> ApiCursorPaginationResponse<T> success(
		int status, CursorPageDto<T> page) {
		return ApiCursorPaginationResponse.<T>builder()
			.status(status)
			.message("Request processed successfully.")
			.data(page.getData())
			.timestamp(LocalDateTime.now())
			.hasPrev(page.hasPrev())
			.hasNext(page.hasNext())
			.total(page.getTotal())
			.prevCursor(page.getPrevCursor())
			.nextCursor(page.getNextCursor())
			.build();
	}

	public static <T> ApiCursorPaginationResponse<T> error(int status, String message) {
		return ApiCursorPaginationResponse.<T>builder()
			.status(status)
			.message(message)
			.timestamp(LocalDateTime.now())
			.build();
	}
}

