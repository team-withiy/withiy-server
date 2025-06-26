package com.server.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class CoupleRestoreStatusDto {
    @Schema(description = "커플 고유 ID", example = "1")
    private Long coupleId;
    @Schema(description = "복구 가능 여부", example = "true")
    private boolean restorable;
    @Schema(description = "삭제된 날짜", example = "2025-04-29T12:34:56")
    private LocalDateTime deletedAt;
}
