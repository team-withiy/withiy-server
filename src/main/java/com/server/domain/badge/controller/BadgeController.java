package com.server.domain.badge.controller;

import com.server.domain.badge.dto.BadgeResponseDto;
import com.server.domain.badge.dto.BadgeSelectionRequestDto;
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

import java.util.List;

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
                                             @Valid @RequestBody BadgeSelectionRequestDto requestDto) {

        badgeService.claimBadge(user, requestDto.getBadgeType(), requestDto.getCharacterType());
        return ApiResponseDto.success(HttpStatus.CREATED.value(),
                String.format("배지 '%s'를 획득했습니다.", requestDto.getBadgeType()));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/main")
    @Operation(summary = "메인 배지 변경",
        description = "사용자가 보유한 배지 중 하나를 메인 배지로 변경합니다.")
    public ApiResponseDto<String> updateMainBadge(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody BadgeSelectionRequestDto requestDto) {
        badgeService.updateMainBadge(user, requestDto.getBadgeType(), requestDto.getCharacterType());
        return ApiResponseDto.success(HttpStatus.OK.value(),
                String.format("메인 배지를 '%s'로 변경했습니다.", requestDto.getBadgeType()));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/all")
    @Operation(summary = "모든 배지 조회",
        description = "사용자가 획득한 배지 정보도 포함하여 모든 배지 정보를 조회합니다.")
    public ApiResponseDto<List<BadgeResponseDto>> getAllBadges(
        @AuthenticationPrincipal User user) {
        List<BadgeResponseDto> badges = badgeService.getBadgeListWithUserInfo(user);
        return ApiResponseDto.success(HttpStatus.OK.value(), badges);
    }
}
