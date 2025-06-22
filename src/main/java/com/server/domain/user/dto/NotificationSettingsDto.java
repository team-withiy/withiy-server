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
public class NotificationSettingsDto {
    @Schema(description = "데이트 알림 설정", example = "true")
    @NotNull(message = "데이트 알림 설정은 필수 항목입니다.")
    private Boolean dateNotificationEnabled;
    @Schema(description = "이벤트 알림 설정", example = "true")
    @NotNull(message = "이벤트 알림 설정은 필수 항목입니다.")
    private Boolean eventNotificationEnabled;
}
