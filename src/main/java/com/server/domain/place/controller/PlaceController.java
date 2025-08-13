package com.server.domain.place.controller;

import com.server.domain.place.dto.CreatePlaceByUserDto;
import com.server.domain.place.dto.CreatePlaceDto;
import com.server.domain.place.dto.CreatePlaceResponse;
import com.server.domain.place.dto.GetPlaceDetailResponse;
import com.server.domain.place.dto.PlaceDetailDto;
import com.server.domain.place.dto.PlaceDto;
import com.server.domain.place.dto.PlaceFocusDto;
import com.server.domain.place.dto.RegisterPlaceDto;
import com.server.domain.place.dto.UpdatePlaceDto;
import com.server.domain.place.service.PlaceFacade;
import com.server.domain.place.service.PlaceService;
import com.server.domain.user.entity.User;
import com.server.global.dto.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/places")
public class PlaceController {

	private final PlaceService placeService;
	private final PlaceFacade placeFacade;

	// TODO: 현재는 USER와 ADMIN 권한을 모두 허용 중이며, 운영 환경에서는 ADMIN 권한만 허용하도록 변경 예정
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.OK)
	@PostMapping("/admin")
	@Operation(summary = "장소 생성 api", description = "관리자가 장소 등록할 수 있는 api, 하위카테고리 선택")
	public ApiResponseDto<CreatePlaceResponse> createPlace(@AuthenticationPrincipal User user,
		@RequestBody CreatePlaceDto createPlaceDto) {
		CreatePlaceResponse response = placeFacade.registerPlace(user, createPlaceDto);
		return ApiResponseDto.success(HttpStatus.OK.value(), response);
	}

	@PreAuthorize("hasRole('USER')")
	@ResponseStatus(HttpStatus.OK)
	@PostMapping("/create")
	@Operation(summary = "사용자 장소 최초 등록 api", description = "사용자가 처음으로 등록하는 장소 api")
	public ApiResponseDto<PlaceDto> createPlaceFirst(@AuthenticationPrincipal User user,
		@RequestBody CreatePlaceByUserDto createPlaceByUserDto) {
		PlaceDto placeDto = placeService.createPlaceFirst(user, createPlaceByUserDto);
		return ApiResponseDto.success(HttpStatus.OK.value(), placeDto);
	}

	@PreAuthorize("hasRole('USER')")
	@ResponseStatus(HttpStatus.OK)
	@PostMapping
	@Operation(summary = "사용자 장소 등록 api", description = "등록되어 있는 장소에 대한 사진/리뷰 저장하는 api")
	public ApiResponseDto<PlaceDto> registerPlace(@AuthenticationPrincipal User user,
		@RequestBody RegisterPlaceDto registerPlaceDto) {
		PlaceDto placeDto = placeService.registerPlace(user, registerPlaceDto);
		return ApiResponseDto.success(HttpStatus.OK.value(), placeDto);
	}


	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/focus")
	@Operation(summary = "지도 위 포커스 화면 장소 조회", description = "sw(남서쪽), ne(북동쪽) longitude, latitude 정보를 받아 그 사이에 있는 장소 정보 조회")
	public ApiResponseDto<List<PlaceFocusDto>> getMapFocusPlaces(@RequestParam String swLat,
		@RequestParam String swLng, @RequestParam String neLat, @RequestParam String neLng) {
		List<PlaceFocusDto> placeFocusDtos = placeService.getMapFocusPlaces(swLat, swLng, neLat,
			neLng);
		return ApiResponseDto.success(HttpStatus.OK.value(), placeFocusDtos);
	}


	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/{placeId}")
	@Operation(summary = "특정 장소 정보 가져오기", description = "장소 id를 받아 특정 장소 간단한 정보 조회")
	public ApiResponseDto<PlaceDto> getMapFocusPlaces(@PathVariable Long placeId,
		@AuthenticationPrincipal User user) {

		return ApiResponseDto.success(HttpStatus.OK.value(),
			placeService.getPlaceSimpleDetail(placeId));
	}

	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/detail/{placeId}")
	@Operation(summary = "특정 장소 상세 정보 가져오기", description = "장소 id를 받아 특정 장소 자세한 정보 조회")
	public ApiResponseDto<GetPlaceDetailResponse> getPlaceDetail(@PathVariable Long placeId) {
		GetPlaceDetailResponse response = placeFacade.getPlaceDetail(placeId);

		return ApiResponseDto.success(HttpStatus.OK.value(), response);
	}


	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.OK)
	@PatchMapping("/{placeId}")
	@Operation(summary = "특정 장소 정보 수정", description = "장소 id와 수정하고 싶은 컬럼을 받아 장소 정보 수정")
	public ApiResponseDto<PlaceDetailDto> updatePlace(@PathVariable Long placeId,
		@RequestBody UpdatePlaceDto updatePlaceDto) {
		PlaceDetailDto placeDetailDto = placeService.updatePlace(placeId, updatePlaceDto);
		return ApiResponseDto.success(HttpStatus.OK.value(), placeDetailDto);

	}

	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.OK)
	@DeleteMapping("/{placeId}")
	@Operation(summary = "특정 장소 삭제", description = "장소 id를 받아 특정 장소 삭제")
	public ApiResponseDto<String> deletePlace(@PathVariable Long placeId) {
		String result = placeService.deletePlace(placeId);
		return ApiResponseDto.success(HttpStatus.OK.value(), result);
	}

	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/{placeId}/bookmark")
	@Operation(summary = "장소 북마크 여부 조회", description = "장소 id를 받아 사용자가 해당 장소를 북마크했는지 여부 조회")
	public ApiResponseDto<Boolean> isBookmarked(@PathVariable Long placeId,
		@AuthenticationPrincipal User user) {
		Boolean isBookmarked = placeService.isBookmarked(placeId, user);
		return ApiResponseDto.success(HttpStatus.OK.value(), isBookmarked);
	}
}
