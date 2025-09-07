package com.server.global.pagination.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "커서 기반 페이지네이션 요청")
public class ApiCursorPaginationRequest implements PaginationRequest {

	@Schema(description = "커서 위치, null일 경우 첫 페이지", example = "123")
	private Long cursor;
	@Schema(description = "한 번에 가져올 데이터 수", example = "10", defaultValue = "10")
	private int limit;
	@Schema(description = "이전 페이지로 이동 여부, false일 경우 다음 페이지", example = "false", defaultValue = "false")
	private Boolean prev;
}
