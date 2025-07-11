package com.server.domain.badge.dto;

import com.server.domain.badge.entity.BadgeType;
import com.server.domain.badge.entity.CharacterType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class BadgeSelectionRequestDto {
    @NotNull
    private BadgeType badgeType;

    private CharacterType characterType;
}
