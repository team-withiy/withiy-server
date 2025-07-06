package com.server.domain.badge.controller;

import com.server.domain.badge.dto.BadgeClaimRequestDto;
import com.server.domain.badge.service.BadgeService;
import com.server.domain.user.entity.User;
import com.server.global.dto.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/badges")
@Slf4j
@Tag(name = "Badge", description = "배지 관련 API")
public class BadgeController {
    private final BadgeService badgeService;

    // 배지 관련 API 메서드들을 여기에 추가할 수 있습니다.
    // 예: 배지 목록 조회, 특정 배지 정보 조회, 사용자 배지 획득 등
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "배지 획득",
        description = "배지 발급 조건을 만족하면 배지를 획득합니다.")
    public ApiResponseDto<String> claimBadge(@AuthenticationPrincipal User user,
                                                   @Valid @RequestBody BadgeClaimRequestDto requestDto) {

        badgeService.claimBadge(user, requestDto.getBadgeType(), requestDto.getCharacterType());
        return ApiResponseDto.success(HttpStatus.CREATED.value(),
                String.format("배지 '%s'를 획득했습니다.", requestDto.getBadgeType()));
    }
}
