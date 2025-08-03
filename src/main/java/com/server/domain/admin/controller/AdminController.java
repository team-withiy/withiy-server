package com.server.domain.admin.controller;

import com.server.domain.admin.dto.ActiveContentsResponse;
import com.server.domain.admin.service.AdminFacade;
import com.server.domain.user.entity.User;
import com.server.global.dto.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/admins")
public class AdminController {
    private final AdminFacade adminFacadeService;

    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/contents/active")
    @Operation(summary = "운영 중 콘텐츠 조회", description = "현재 운영 중인 장소와 코스를 조회합니다.")
    public ApiResponseDto<ActiveContentsResponse> getActiveContents(@RequestParam  String category,
                                                                    @RequestParam String keyword) {
        ActiveContentsResponse response = adminFacadeService.getActiveContents(category, keyword);
        return ApiResponseDto.success(HttpStatus.OK.value(), response);
    }
}
