package com.server.domain.admin.controller;

import com.server.domain.admin.dto.ActiveContentsResponse;
import com.server.domain.admin.service.AdminFacade;
import com.server.domain.place.dto.CreatePlaceDto;
import com.server.domain.place.dto.CreatePlaceResponse;
import com.server.domain.user.entity.User;
import com.server.global.dto.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
public class AdminController {

	private final AdminFacade adminFacade;

	// TODO: 현재는 USER와 ADMIN 권한을 모두 허용 중이며, 운영 환경에서는 ADMIN 권한만 허용하도록 변경 예정
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/contents/active")
	@Operation(summary = "운영 중 콘텐츠 조회", description = "현재 운영 중인 장소와 코스를 조회합니다.")
	public ApiResponseDto<ActiveContentsResponse> getActiveContents(
		@AuthenticationPrincipal User user,
		@Parameter(description = "콘텐츠 카테고리", example = "Shop, Restaurant") @RequestParam String category,
		@Parameter(description = "검색 키워드", example = "스타필드") @RequestParam String keyword) {
		ActiveContentsResponse response = adminFacade.getActiveContents(category, keyword);
		return ApiResponseDto.success(HttpStatus.OK.value(), response);
	}

	// TODO: 현재는 USER와 ADMIN 권한을 모두 허용 중이며, 운영 환경에서는 ADMIN 권한만 허용하도록 변경 예정
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.OK)
	@PostMapping("/places")
	@Operation(summary = "장소 생성 api", description = "관리자가 장소 등록할 수 있는 api, 하위카테고리 선택")
	public ApiResponseDto<CreatePlaceResponse> createPlace(@AuthenticationPrincipal User user,
		@RequestBody CreatePlaceDto createPlaceDto) {
		CreatePlaceResponse response = adminFacade.registerPlace(user, createPlaceDto);
		return ApiResponseDto.success(HttpStatus.OK.value(), response);
	}
}
