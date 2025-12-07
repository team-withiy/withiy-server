package com.server.domain.search.controller;

import com.server.domain.search.dto.SearchHistoryDto;
import com.server.domain.search.dto.SearchResultResponse;
import com.server.domain.search.dto.request.SearchResultRequest;
import com.server.domain.search.service.SearchFacade;
import com.server.domain.user.entity.User;
import com.server.global.dto.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Tag(name = "Search", description = "검색 관련 API")
public class SearchController {

	private final SearchFacade searchFacade;

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "[공용] 장소/코스 검색 결과 반환", description = "키워드, 필터, 정렬 기준에 따른 장소 및 코스 검색 결과를 반환합니다.")
	public ApiResponseDto<SearchResultResponse> getSearchResults(@AuthenticationPrincipal User user,
		@ModelAttribute @Valid SearchResultRequest request) {

		SearchResultResponse searchResponseDto = searchFacade.search(user,
			request);
		return ApiResponseDto.success(HttpStatus.OK.value(), searchResponseDto);
	}

	@GetMapping("/history")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "최근 검색어 조회", description = "사용자의 최근 검색어 목록을 반환합니다.")
	public ApiResponseDto<List<SearchHistoryDto>> getRecentSearches(
		@AuthenticationPrincipal User user) {
		return ApiResponseDto.success(HttpStatus.OK.value(), searchFacade.getRecentSearches(user));
	}

}
