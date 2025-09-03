package com.server.global.pagination.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "커서 기반 페이지네이션 응답")
public class ApiCursorPaginationResponse<T, ID> implements PaginationResponse<T> {

	@Schema(description = "HTTP 상태 코드", example = "200")
	private int status;
	@Schema(description = "응답 메시지", example = "Request processed successfully.")
	private String message;
	@Schema(description = "데이터 목록")
	private List<T> data;
	@Schema(description = "응답 생성 시간", example = "2023-10-05T14:48:00")
	private LocalDateTime timestamp;
	@Schema(description = "이전 페이지 존재 여부", example = "true")
	private boolean hasPrev;
	@Schema(description = "다음 페이지 존재 여부", example = "true")
	private boolean hasNext;
	@Schema(description = "총 데이터 수", example = "100")
	private long total;
	@Schema(description = "이전 페이지 커서", example = "123")
	private ID prevCursor;
	@Schema(description = "다음 페이지 커서", example = "456")
	private ID nextCursor;

	public static <T, ID> ApiCursorPaginationResponse<T, ID> success(
		int status, CursorPageDto<T, ID> page) {
		return ApiCursorPaginationResponse.<T, ID>builder()
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
}

