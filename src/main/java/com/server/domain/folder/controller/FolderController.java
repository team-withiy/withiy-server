package com.server.domain.folder.controller;

import com.server.domain.folder.dto.CreateFolderDto;
import com.server.domain.folder.dto.FolderDto;
import com.server.domain.folder.dto.UpdateFolderDto;
import com.server.domain.folder.service.FolderService;
import com.server.domain.place.dto.CreatePlaceDto;
import com.server.domain.place.dto.PlaceDto;
import com.server.domain.user.entity.User;
import com.server.global.dto.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/folders")
public class FolderController {

    private final FolderService folderService;

    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/with-place")
    @Operation(summary = "장소 저장하면서 폴더 생성 api", description = "장소 저장하면서 폴더 생성")
    public ApiResponseDto<FolderDto> createFolderAndBookmarkPlace(@AuthenticationPrincipal User user,
                                                                  @RequestBody CreateFolderDto createFolderDto, @RequestParam Long placeId) {
        FolderDto folderDto = folderService.createFolderAndBookmarkPlace(user, createFolderDto, placeId);
        return ApiResponseDto.success(HttpStatus.OK.value(), folderDto);
    }


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
    public ApiResponseDto<FolderDto> updateFolder(@PathVariable Long folderId, @AuthenticationPrincipal User user,
                                                  @RequestBody UpdateFolderDto updateFolderDto) {
        FolderDto folderDto = folderService.updateFolder(folderId, user, updateFolderDto);
        return ApiResponseDto.success(HttpStatus.OK.value(), folderDto);
    }

    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{folderId}")
    @Operation(summary = "폴더 삭제 api", description = "특정 폴더 삭제, 폴더 안에 저장되어있던 장소도 저장 해제(해당폴더 저장)")
    public ApiResponseDto<String> deleteFolder(@PathVariable Long folderId, @AuthenticationPrincipal User user) {
        String result = folderService.deleteFolder(folderId, user);
        return ApiResponseDto.success(HttpStatus.OK.value(), result);
    }

}
