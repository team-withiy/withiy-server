package com.server.domain.search.controller;

import com.server.domain.search.dto.SearchRequestDto;
import com.server.domain.search.dto.SearchResponseDto;
import com.server.domain.search.service.SearchFacadeService;
import com.server.domain.user.entity.User;
import com.server.global.dto.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Tag(name = "Search", description = "검색 관련 API")
public class SearchController {

	private final SearchFacadeService searchFacadeService;


	/**
	 * 검색 API
	 *
	 * @param user             인증된 사용자 정보
	 * @param searchRequestDto 검색 요청 DTO
	 * @return 검색 결과 DTO
	 */
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "검색", description = "사용자의 검색 요청에 대한 결과를 반환합니다.")
	@GetMapping
	public ApiResponseDto<SearchResponseDto> search(@AuthenticationPrincipal User user,
		@RequestParam SearchRequestDto searchRequestDto) {

		SearchResponseDto searchResponseDto = searchFacadeService.search(user, searchRequestDto);
		return ApiResponseDto.success(HttpStatus.OK.value(), searchResponseDto);
	}

}
