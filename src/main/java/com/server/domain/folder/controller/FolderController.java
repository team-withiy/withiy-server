package com.server.domain.folder.controller;

import com.server.domain.folder.dto.CreateFolderDto;
import com.server.domain.folder.dto.FolderDto;
import com.server.domain.folder.dto.GetFolderPlacesResponse;
import com.server.domain.folder.dto.UpdateFolderDto;
import com.server.domain.folder.service.FolderFacade;
import com.server.domain.folder.service.FolderService;
import com.server.domain.user.entity.User;
import com.server.global.dto.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
	public ApiResponseDto<FolderDto> createFolder(@AuthenticationPrincipal User user,
		@RequestBody CreateFolderDto createFolderDto) {
		FolderDto folderDto = folderService.createFolder(user, createFolderDto);
		return ApiResponseDto.success(HttpStatus.OK.value(), folderDto);
	}

	@PreAuthorize("hasRole('USER')")
	@ResponseStatus(HttpStatus.OK)
	@PatchMapping("/{folderId}")
	@Operation(summary = "폴더 수정 api", description = "폴더 이름/색상 수정")
	public ApiResponseDto<FolderDto> updateFolder(@PathVariable Long folderId,
		@AuthenticationPrincipal User user,
		@RequestBody UpdateFolderDto updateFolderDto) {
		FolderDto folderDto = folderService.updateFolder(folderId, user, updateFolderDto);
		return ApiResponseDto.success(HttpStatus.OK.value(), folderDto);
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
	public ApiResponseDto<List<FolderDto>> getFolders(@AuthenticationPrincipal User user) {
		return ApiResponseDto.success(HttpStatus.OK.value(), folderService.getFolders(user));
	}

	@PreAuthorize("hasRole('USER')")
	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/{folderId}")
	@Operation(summary = "폴더 조회 api", description = "특정 폴더 조회")
	public ApiResponseDto<GetFolderPlacesResponse> getFolder(@PathVariable Long folderId,
		@AuthenticationPrincipal User user) {

		return ApiResponseDto.success(HttpStatus.OK.value(),
			folderFacade.getFolder(folderId, user));
	}

	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/{folderId}/places/{placeId}")
	@Operation(summary = "폴더에 장소 저장 api", description = "특정 폴더에 장소 저장")
	public ApiResponseDto<String> savePlaceInFolder(@PathVariable Long folderId,
		@PathVariable Long placeId, @AuthenticationPrincipal User user) {
		String result = folderFacade.savePlaceInFolder(folderId, placeId, user);
		return ApiResponseDto.success(HttpStatus.OK.value(), result);
	}
}
