package com.server.domain.user.controller;

import com.server.domain.user.dto.CoupleConnectionRequestDto;
import com.server.domain.user.dto.CoupleDto;
import com.server.domain.user.dto.FirstMetDateUpdateDto;
import com.server.domain.user.dto.RestoreCoupleDto;
import com.server.domain.user.entity.User;
import com.server.domain.user.service.CoupleService;
import com.server.global.dto.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/couples")
@Slf4j
@Tag(name = "Couple", description = "커플 관련 API")
public class CoupleController {

	private final CoupleService coupleService;

	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping
	@Operation(summary = "커플 연결",
		description = "사용자 코드를 사용하여 커플을 연결합니다. 선택적으로 처음 만난 날짜를 지정할 수 있습니다.")
	public ApiResponseDto<CoupleDto> connectCouple(@AuthenticationPrincipal User user,
		@Valid @RequestBody CoupleConnectionRequestDto requestDto) {
		CoupleDto coupleDto = coupleService.connectCouple(user, requestDto.getPartnerCode(),
			requestDto.getFirstMetDate());

		return ApiResponseDto.success(HttpStatus.CREATED.value(), coupleDto);
	}

	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.OK)
	@GetMapping
	@Operation(summary = "커플 정보 조회", description = "현재 로그인한 사용자의 커플 관계 정보를 조회합니다.")
	public ApiResponseDto<CoupleDto> getCouple(@AuthenticationPrincipal User user) {
		CoupleDto coupleDto = coupleService.getCouple(user);

		return ApiResponseDto.success(HttpStatus.OK.value(), coupleDto);
	}

	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.OK)
	@DeleteMapping
	@Operation(summary = "커플 연결 해제", description = "현재 로그인한 사용자의 커플 관계를 해제합니다.")
	public ApiResponseDto<Long> disconnectCouple(@AuthenticationPrincipal User user) {
		Long coupleId = coupleService.disconnectCouple(user);

		return ApiResponseDto.success(HttpStatus.OK.value(), coupleId);
	}

	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.OK)
	@PostMapping("/restore")
	@Operation(summary = "커플 연결 복구", description = "현재 로그인한 사용자의 커플 관계를 복구합니다.")
	public ApiResponseDto<Long> restoreCouple(@AuthenticationPrincipal User user,
		@RequestBody RestoreCoupleDto requestDto) {
		Long coupleId = coupleService.restoreCouple(user, requestDto.isRestore());

		return ApiResponseDto.success(HttpStatus.OK.value(), coupleId);
	}

	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.OK)
	@PatchMapping("/first-met-date")
	@Operation(summary = "처음 만난 날짜 설정", description = "이미 연결된 커플의 처음 만난 날짜를 설정하거나 변경합니다.")
	public ApiResponseDto<CoupleDto> updateFirstMetDate(@AuthenticationPrincipal User user,
		@Valid @RequestBody FirstMetDateUpdateDto requestDto) {

		CoupleDto coupleDto = coupleService.updateFirstMetDate(user, requestDto.getFirstMetDate());

		return ApiResponseDto.success(HttpStatus.OK.value(), coupleDto);
	}
}
