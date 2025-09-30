package com.server.domain.place.controller;

import com.server.domain.photo.dto.PhotoDto;
import com.server.domain.place.dto.FolderIdsRequest;
import com.server.domain.place.dto.GetPlaceDetailResponse;
import com.server.domain.place.dto.PlaceDetailDto;
import com.server.domain.place.dto.PlaceDto;
import com.server.domain.place.dto.RegisterPhotoRequest;
import com.server.domain.place.dto.UpdatePlaceDto;
import com.server.domain.place.dto.request.PlaceFocusRequest;
import com.server.domain.place.dto.response.PlaceFocusResponse;
import com.server.domain.place.service.PlaceFacade;
import com.server.domain.place.service.PlaceService;
import com.server.domain.review.dto.ReviewDto;
import com.server.domain.user.entity.User;
import com.server.global.dto.ApiResponseDto;
import com.server.global.pagination.dto.ApiCursorPaginationRequest;
import com.server.global.pagination.dto.ApiCursorPaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/places")
public class PlaceController {

	private final PlaceService placeService;
	private final PlaceFacade placeFacade;

	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/{placeId}")
	@Operation(summary = "특정 장소 정보 가져오기", description = "장소 id를 받아 특정 장소 간단한 정보 조회")
	public ApiResponseDto<PlaceDto> getMapFocusPlaces(@PathVariable Long placeId,
		@AuthenticationPrincipal User user) {

		return ApiResponseDto.success(HttpStatus.OK.value(),
			placeService.getPlaceSimpleDetail(placeId));
	}

	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/{placeId}/detail")
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
	@GetMapping("/{placeId}/bookmarks")
	@Operation(summary = "장소 북마크 여부 조회", description = "장소 id를 받아 사용자가 해당 장소를 북마크했는지 여부 조회")
	public ApiResponseDto<Boolean> isBookmarked(@PathVariable Long placeId,
		@AuthenticationPrincipal User user) {
		Boolean isBookmarked = placeFacade.isBookmarked(placeId, user);
		return ApiResponseDto.success(HttpStatus.OK.value(), isBookmarked);
	}

	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.OK)
	@PutMapping("/{placeId}/bookmarks")
	@Operation(summary = "장소 북마크 추가/삭제", description = "장소가 북마크에 추가되어 있으면 삭제하고, 추가되어 있지 않으면 추가합니다.")
	public ApiResponseDto<String> toggleBookmark(@RequestBody FolderIdsRequest request,
		@PathVariable Long placeId, @AuthenticationPrincipal User user) {
		String result = placeFacade.updatePlaceFolders(request.getFolderIds(), placeId, user);
		return ApiResponseDto.success(HttpStatus.OK.value(), result);
	}

	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.OK)
	@PostMapping("/{placeId}/photos")
	@Operation(summary = "장소 전체 공개 사진 등록", description = "장소에 사진을 등록합니다. 사진은 전체 공개로 설정됩니다.")
	public ApiResponseDto<String> registerPhotos(@AuthenticationPrincipal User user,
		@PathVariable Long placeId, @RequestBody RegisterPhotoRequest request) {

		return ApiResponseDto.success(HttpStatus.OK.value(),
			placeFacade.registerPhotos(user, placeId, request));
	}

	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/{placeId}/photos")
	public ApiCursorPaginationResponse<PhotoDto, Long> getPlacePhotos(@PathVariable Long placeId,
		@Valid @ModelAttribute ApiCursorPaginationRequest pageRequest) {
		return ApiCursorPaginationResponse.success(HttpStatus.OK.value(),
			placeFacade.getPlacePhotos(placeId, pageRequest));
	}

	@GetMapping("/{placeId}/photos/{photoId}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponseDto<PhotoDto> getPlacePhoto(@PathVariable Long placeId,
		@PathVariable Long photoId) {
		return ApiResponseDto.success(HttpStatus.OK.value(),
			placeFacade.getPlacePhoto(placeId, photoId));
	}

	@GetMapping("/{placeId}/reviews")
	@ResponseStatus(HttpStatus.OK)
	public ApiCursorPaginationResponse<ReviewDto, Long> getPlaceReviews(@PathVariable Long placeId,
		@ModelAttribute ApiCursorPaginationRequest pageRequest) {
		return ApiCursorPaginationResponse.success(HttpStatus.OK.value(),
			placeFacade.getPlaceReviews(placeId, pageRequest));
	}

	@GetMapping("/focus")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "지도 위 포커스 화면 장소 조회", description = "사용자의 현재 위치를 기반으로 근처 장소를 조회합니다.")
	public ApiResponseDto<PlaceFocusResponse> getFocusPlaces(@AuthenticationPrincipal User user,
		@ModelAttribute PlaceFocusRequest request) {
		PlaceFocusResponse response = placeFacade.getFocusPlaces(user, request);
		return ApiResponseDto.success(HttpStatus.OK.value(), response);
	}
}
