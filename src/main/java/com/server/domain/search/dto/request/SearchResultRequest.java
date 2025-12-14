package com.server.domain.search.dto.request;

import com.server.domain.search.entity.SearchPageType;
import com.server.domain.search.entity.SearchTargetType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SearchResultRequest {

	@Schema(description = "검색어", example = "홍대입구역")
	private String keyword; // 검색어
	@Schema(description = "검색 페이지 타입", example = "main or date_schedule")
	@NotNull(message = "검색 페이지는 필수입니다.")
	private SearchPageType pageType; // 검색 소스 (메인, 데이트 일정)
	@Schema(description = "검색 대상 타입", example = "place or route")
	@NotNull(message = "검색 대상은 필수입니다.")
	private SearchTargetType targetType;
}
