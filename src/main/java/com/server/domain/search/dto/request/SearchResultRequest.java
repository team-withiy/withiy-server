package com.server.domain.search.dto.request;

import com.server.domain.search.dto.SearchSource;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SearchResultRequest {

	@Schema(description = "검색어", example = "홍대입구역")
	private String keyword; // 검색어
	@Schema(description = "검색 소스", example = "MAIN or DATE_SCHEDULE")
	@NotNull
	private SearchSource source; // 검색 소스 (메인, 데이트 일정)
}
