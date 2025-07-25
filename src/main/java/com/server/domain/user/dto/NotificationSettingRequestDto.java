package com.server.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSettingRequestDto {
    @Schema(description = "데이트 알림 설정", example = "true")
    private Boolean dateNotificationEnabled;
    @Schema(description = "이벤트 알림 설정", example = "true")
    private Boolean eventNotificationEnabled;
}
