package com.server.domain.badge.dto;

import com.server.domain.badge.entity.BadgeType;
import com.server.domain.badge.entity.CharacterType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BadgeResponseDto {
    @Schema(description = "배지 타입", example = "WITHIY_COUPLE")
    private BadgeType badgeType;
    @Schema(description = "배지 이름", example = "위디커플")
    private String badgeName;
    @Schema(description = "배지 설명 문구", example = "커플 연결을 하면 받을 수 있어요!")
    private String badgeDescription;
    @Schema(description = "아이템 이름 또는 이미지 설명", example = "하트 머리핀 or 머리띠")
    private String item;
    @Schema(description = "해당 유저가 이 배지를 보유하고 있는지 여부", example = "true")
    private boolean isOwned;
    @Schema(description = "메인 배지로 설정되어 있는지 여부", example = "false")
    private boolean isMain;
    @Schema(description = "적용된 캐릭터 타입 (BEAR 또는 RABBIT)", example = "BEAR")
    private CharacterType characterType; // null 가능
}
