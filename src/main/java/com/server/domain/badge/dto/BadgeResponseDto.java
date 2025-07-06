package com.server.domain.badge.dto;

import com.server.domain.badge.entity.BadgeType;
import com.server.domain.badge.entity.CharacterType;
import lombok.Builder;

@Builder
public class BadgeResponseDto {
    private BadgeType badgeType;
    private String badgeLabel;
    private String item;
    private boolean isOwned;
    private boolean isMain;
    private CharacterType characterType; // null 가능
}
