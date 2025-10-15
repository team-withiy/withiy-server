package com.server.domain.admin.controller;

import com.server.domain.admin.dto.ActiveContentsResponse;
import com.server.domain.admin.service.AdminFacade;
import com.server.domain.place.dto.CreatePlaceDto;
import com.server.domain.place.dto.CreatePlaceResponse;
import com.server.domain.user.entity.User;
import com.server.global.dto.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "관리자 관련 API")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

	private final AdminFacade adminFacade;

	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/contents/active")
	@Operation(summary = "[관리자] 운영 중 콘텐츠 조회", description = "현재 운영 중인 장소와 코스를 조회합니다.")
	public ApiResponseDto<ActiveContentsResponse> getActiveContents(
		@AuthenticationPrincipal User user,
		@Parameter(description = "콘텐츠 카테고리", example = "Shop, Restaurant") @RequestParam String category,
		@Parameter(description = "검색 키워드", example = "스타필드") @RequestParam(required = false) String keyword) {
		ActiveContentsResponse response = adminFacade.getActiveContents(category, keyword);
		return ApiResponseDto.success(HttpStatus.OK.value(), response);
	}

	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.OK)
	@PostMapping("/places")
	@Operation(summary = "[관리자] 장소 생성 api", description = "관리자가 장소 등록할 수 있는 api, 하위카테고리 선택")
	public ApiResponseDto<CreatePlaceResponse> createPlace(@AuthenticationPrincipal User user,
		@RequestBody CreatePlaceDto createPlaceDto) {
		CreatePlaceResponse response = adminFacade.registerPlace(user, createPlaceDto);
		return ApiResponseDto.success(HttpStatus.OK.value(), response);
	}
}
