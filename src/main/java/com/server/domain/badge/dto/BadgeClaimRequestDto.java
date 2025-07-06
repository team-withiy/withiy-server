package com.server.domain.badge.dto;

import com.server.domain.badge.entity.BadgeType;
import com.server.domain.badge.entity.CharacterType;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class BadgeClaimRequestDto {
    @NotBlank
    private BadgeType badgeType;
    @NotBlank
    private CharacterType characterType;
}
