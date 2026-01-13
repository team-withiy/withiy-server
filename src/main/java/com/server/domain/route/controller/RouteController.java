package com.server.domain.route.controller;

import com.server.domain.route.dto.RouteDetailDto;
import com.server.domain.route.service.RouteFacade;
import com.server.domain.user.entity.User;
import com.server.global.dto.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Course", description = "코스 관련 API")
public class RouteController {

	private final RouteFacade routeFacade;

	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/{courseId}")
	@Operation(summary = "코스 정보 가져오기", description = "해당 id 코스 dto 반환")
	public ApiResponseDto<RouteDetailDto> getCourseDetail(@PathVariable Long courseId) {
		RouteDetailDto courseDetailDto = routeFacade.getCourseDetail(courseId);
		log.info("Returning API response with CourseDetailDto: {}", courseDetailDto);

		return ApiResponseDto.success(HttpStatus.OK.value(), courseDetailDto);
	}

	@PreAuthorize("hasRole('USER')")
	@SecurityRequirement(name = "bearerAuth")
	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/{courseId}/bookmarks")
	@Operation(summary = "[사용자] 코스 북마크 여부 조회", description = "코스 id를 받아 사용자가 해당 코스를 북마크했는지 여부 조회")
	public ApiResponseDto<Boolean> isBookmarked(@PathVariable Long courseId,
		@AuthenticationPrincipal User user) {
		return ApiResponseDto.success(HttpStatus.OK.value(),
			routeFacade.isBookmarked(courseId, user));
	}

	@PreAuthorize("hasRole('USER')")
	@SecurityRequirement(name = "bearerAuth")
	@ResponseStatus(HttpStatus.OK)
	@PutMapping("/{courseId}/bookmarks")
	@Operation(summary = "[사용자] 코스 북마크 추가/삭제", description = "코스가 북마크에 추가되어 있으면 삭제하고, 추가되어 있지 않으면 추가합니다.")
	public ApiResponseDto<String> toggleBookmark(@PathVariable Long courseId,
		@AuthenticationPrincipal User user) {
		return ApiResponseDto.success(HttpStatus.OK.value(),
			routeFacade.toggleBookmark(courseId, user));
	}
}
