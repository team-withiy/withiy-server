package com.server.domain.badge.controller;

import com.server.domain.badge.entity.BadgeType;
import com.server.domain.badge.entity.CharacterType;
import com.server.domain.badge.service.BadgeService;
import com.server.domain.user.entity.User;
import com.server.global.dto.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "배지 획득",
        description = "배지 발급 조건을 만족하면 배지를 획득합니다.")
    public ApiResponseDto<String> claimBadge(@AuthenticationPrincipal User user,
                                             @RequestParam BadgeType badgeType,
                                             @RequestParam CharacterType characterType) {

        badgeService.claimBadge(user, badgeType, characterType);
        return ApiResponseDto.success(HttpStatus.CREATED.value(),
                String.format("배지 '%s'를 획득했습니다.", badgeType));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/main")
    @Operation(summary = "메인 배지 변경",
        description = "사용자가 보유한 배지 중 하나를 메인 배지로 변경합니다.")
    public ApiResponseDto<String> updateMainBadge(
            @AuthenticationPrincipal User user,
            @RequestParam BadgeType badgeType,
            @RequestParam CharacterType characterType) {
        badgeService.updateMainBadge(user, badgeType, characterType);
        return ApiResponseDto.success(HttpStatus.OK.value(),
                String.format("메인 배지를 '%s'로 변경했습니다.", badgeType));
    }
}
