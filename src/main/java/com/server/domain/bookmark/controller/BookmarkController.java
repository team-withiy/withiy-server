package com.server.domain.bookmark.controller;

import com.server.domain.bookmark.dto.BookmarkedPlaceDto;
import com.server.domain.bookmark.dto.BookmarkedRouteDto;
import com.server.domain.bookmark.service.BookmarkFacade;
import com.server.domain.user.entity.User;
import com.server.global.dto.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
@Tag(name = "Bookmark", description = "북마크 관련 API")
public class BookmarkController {

	private final BookmarkFacade bookmarkFacade;

	@GetMapping("/places")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = " 북마크된 장소 조회", description = "사용자가 북마크한 장소 목록을 반환합니다.")
	public ApiResponseDto<List<BookmarkedPlaceDto>> getBookmarkedPlaces(
		@AuthenticationPrincipal User user) {
		return ApiResponseDto.success(HttpStatus.OK.value(),
			bookmarkFacade.getBookmarkedPlaces(user));
	}

	@GetMapping("/courses")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = " 북마크된 코스 조회", description = "사용자가 북마크한 코스 목록을 반환합니다.")
	public ApiResponseDto<List<BookmarkedRouteDto>> getBookmarkedRoutes(
		@AuthenticationPrincipal User user) {
		return ApiResponseDto.success(HttpStatus.OK.value(),
			bookmarkFacade.getBookmarkedRoutes(user));
	}
}
