package com.server.domain.album.controller;

import com.server.domain.album.dto.AlbumCommentRequest;
import com.server.domain.album.dto.AlbumDetailResponse;
import com.server.domain.album.dto.AlbumPageResponse;
import com.server.domain.album.service.AlbumFacade;
import com.server.domain.user.entity.User;
import com.server.global.dto.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/album")
@Tag(name = "Album", description = "앨범 관련 API")
@SecurityRequirement(name = "bearerAuth")
public class AlbumController {

    private final AlbumFacade albumFacade;

    @GetMapping
    @Operation(summary = "[사용자] 앨범 조회 API", description = "사용자의 앨범을 조회하는 API")
    public ApiResponseDto<AlbumPageResponse> getAlbums(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam String order
    ) throws Exception {
        AlbumPageResponse response = albumFacade.getAlbums(user, page, size, order);
        return ApiResponseDto.success(HttpStatus.OK.value(), response);
    }

    @GetMapping("/{albumId}")
    @Operation(summary = "[사용자] 앨범 상세 조회 API", description = "사용자의 앨범을 상세 조회하는 API")
    public ApiResponseDto<AlbumDetailResponse> getAlbum(
            @AuthenticationPrincipal User user,
            @PathVariable Long albumId
    ) {
        AlbumDetailResponse response = albumFacade.getAlbum(user, albumId);
        return ApiResponseDto.success(HttpStatus.OK.value(), response);
    }

    @DeleteMapping("/{albumId}")
    @Operation(summary = "[사용자] 앨범 삭제 API", description = "사용자의 앨범을 삭제하는 API")
    public ApiResponseDto<AlbumDetailResponse> deleteAlbum(
            @AuthenticationPrincipal User user,
            @PathVariable Long albumId
    ) {
        albumFacade.deleteAlbum(user, albumId);
        return ApiResponseDto.success(HttpStatus.OK.value(), null);
    }

    @PostMapping("/comment")
    @Operation(summary = "[사용자] 앨범 댓글 작성 API", description = "앨범 댓글을 작성하는 API")
    public ApiResponseDto<Void> writeComment(
            @AuthenticationPrincipal User user,
            @RequestBody AlbumCommentRequest request) {
        albumFacade.writeComment(user, request);
        return ApiResponseDto.success(HttpStatus.OK.value(), null);
    }

    @PatchMapping("/comment/{commentId}")
    @Operation(summary = "[사용자] 앨범 댓글 수정 API", description = "앨범 댓글을 수정하는 API")
    public ApiResponseDto<Void> updateComment(
            @AuthenticationPrincipal User user,
            @PathVariable Long commentId,
            @RequestBody AlbumCommentRequest request) {
        albumFacade.updateComment(user, commentId, request);
        return ApiResponseDto.success(HttpStatus.OK.value(), null);
    }

    @DeleteMapping("/comment/{commentId}")
    @Operation(summary = "[사용자] 앨범 댓글 삭제 API", description = "앨범 댓글을 삭제하는 API")
    public ApiResponseDto<Void> deleteComment(
            @AuthenticationPrincipal User user,
            @PathVariable Long commentId) {
        albumFacade.deleteComment(user, commentId);
        return ApiResponseDto.success(HttpStatus.OK.value(), null);
    }
}
