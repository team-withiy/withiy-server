package com.server.domain.search.dto.response;

import com.server.domain.bookmark.dto.BookmarkedRouteDto;
import com.server.domain.bookmark.dto.BookmarkedPlaceDto;
import com.server.domain.search.dto.SearchHistoryDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "검색 초기 응답 DTO")
public class SearchInitResponse {

	@Schema(description = "최근 검색어 목록")
	private List<SearchHistoryDto> recentKeywords;
	@Schema(description = "북마크된 장소 목록")
	private List<BookmarkedPlaceDto> bookmarkedPlaces;
	@Schema(description = "북마크된 루트 목록")
	private List<BookmarkedRouteDto> bookmarkedRoutes;

}
