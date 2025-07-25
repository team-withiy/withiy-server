package com.server.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "계정 복구/초기화 요청 DTO")
public class RestoreAccountDto {

    @Schema(description = "복구 여부 (true: 복구, false: 초기화)", example = "true")
    private boolean restore;
}
