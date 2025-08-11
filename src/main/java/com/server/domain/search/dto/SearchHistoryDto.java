package com.server.domain.search.dto;

import com.server.domain.search.entity.SearchHistory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class SearchHistoryDto {

	@Schema(description = "검색어", example = "홍대입구역")
	private String keyword; // 검색어
	@Schema(description = "생성일시", example = "2023-10-01T12:00:00")
	private String createdAt; // 생성일시

	public SearchHistoryDto(String keyword, String createdAt) {
		this.keyword = keyword;
		this.createdAt = createdAt;
	}

	public static SearchHistoryDto from(SearchHistory searchHistory) {
		return new SearchHistoryDto(
			searchHistory.getKeyword(),
			searchHistory.getCreatedAt().toString()
		);
	}

}
