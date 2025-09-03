package com.server.domain.folder.controller;

import com.server.domain.folder.dto.CreateFolderDto;
import com.server.domain.folder.dto.FolderOptionDto;
import com.server.domain.folder.dto.FolderSummaryDto;
import com.server.domain.folder.dto.PlaceSummaryDto;
import com.server.domain.folder.dto.UpdateFolderDto;
import com.server.domain.folder.service.FolderFacade;
import com.server.domain.folder.service.FolderService;
import com.server.domain.user.entity.User;
import com.server.global.dto.ApiResponseDto;
import com.server.global.pagination.dto.ApiCursorPaginationRequest;
import com.server.global.pagination.dto.ApiCursorPaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
@RequestMapping("/api/folders")
public class FolderController {

	private final FolderService folderService;
	private final FolderFacade folderFacade;

	@PreAuthorize("hasRole('USER')")
	@ResponseStatus(HttpStatus.OK)
	@PostMapping
	@Operation(summary = "폴더 생성 api", description = "폴더 생성")
	public ApiResponseDto<FolderSummaryDto> createFolder(@AuthenticationPrincipal User user,
		@Valid @RequestBody CreateFolderDto createFolderDto) {
		return ApiResponseDto.success(HttpStatus.OK.value(),
			folderService.createFolder(user, createFolderDto));
	}

	@PreAuthorize("hasRole('USER')")
	@ResponseStatus(HttpStatus.OK)
	@PatchMapping("/{folderId}")
	@Operation(summary = "폴더 수정 api", description = "폴더 이름/색상 수정")
	public ApiResponseDto<String> updateFolder(@PathVariable Long folderId,
		@AuthenticationPrincipal User user, @RequestBody UpdateFolderDto updateFolderDto) {
		String result = folderService.updateFolder(folderId, user, updateFolderDto);
		return ApiResponseDto.success(HttpStatus.OK.value(), result);
	}

	@PreAuthorize("hasRole('USER')")
	@ResponseStatus(HttpStatus.OK)
	@DeleteMapping("/{folderId}")
	@Operation(summary = "폴더 삭제 api", description = "특정 폴더 삭제, 폴더 안에 저장되어있던 장소도 저장 해제(해당폴더 저장)")
	public ApiResponseDto<String> deleteFolder(@PathVariable Long folderId,
		@AuthenticationPrincipal User user) {
		String result = folderService.deleteFolder(folderId, user);
		return ApiResponseDto.success(HttpStatus.OK.value(), result);
	}

	@PreAuthorize("hasRole('USER')")
	@ResponseStatus(HttpStatus.OK)
	@GetMapping
	@Operation(summary = "폴더 목록 조회 api", description = "사용자 폴더 목록 조회")
	public ApiResponseDto<List<FolderSummaryDto>> getFolders(@AuthenticationPrincipal User user) {
		return ApiResponseDto.success(HttpStatus.OK.value(),
			folderFacade.getFolderSummaries(user));
	}

	@PreAuthorize("hasRole('USER')")
	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/{folderId}")
	@Operation(summary = "폴더 조회 api", description = "폴더 내 장소들을 커서 페이징으로 조회")
	public ApiCursorPaginationResponse<PlaceSummaryDto, Long> getFolderPlaces(
		@PathVariable Long folderId,
		@AuthenticationPrincipal User user,
		@Valid @ModelAttribute ApiCursorPaginationRequest pageRequest) {

		return ApiCursorPaginationResponse.success(HttpStatus.OK.value(),
			folderFacade.getFolderPlaces(folderId, user, pageRequest));
	}

	@PreAuthorize("hasRole('USER')")
	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/all")
	@Operation(summary = "저장한 모든 장소 조회 api", description = "저장한 모든 장소 조회")
	public ApiCursorPaginationResponse<PlaceSummaryDto, Long> getAllFolderPlaces(
		@AuthenticationPrincipal User user,
		@Valid @ModelAttribute ApiCursorPaginationRequest pageRequest) {

		return ApiCursorPaginationResponse.success(HttpStatus.OK.value(),
			folderFacade.getAllFolderPlaces(user, pageRequest));
	}

	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/select")
	@Operation(summary = "내 폴더 목록 조회 및 각 폴더에 해당 장소 북마크 여부 조회")
	public ApiResponseDto<List<FolderOptionDto>> getFoldersForPlaceSelection(
		@RequestParam Long placeId, @AuthenticationPrincipal User user) {

		return ApiResponseDto.success(HttpStatus.OK.value(),
			folderFacade.getFoldersForPlaceSelection(placeId, user));
	}
}
