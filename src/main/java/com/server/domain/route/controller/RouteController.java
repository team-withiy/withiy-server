package com.server.domain.route.controller;

import com.server.domain.route.dto.RouteDetailDto;
import com.server.domain.route.service.RouteFacade;
import com.server.global.dto.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Slf4j
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
}
