package com.server.domain.user.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.server.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @Schema(description = "사용자 ID", example = "6")
    private Long id;
    @Schema(description = "사용자 닉네임", example = "위디1호")
    private String nickname;
    @Schema(description = "프로필 이미지 URL", example = "https://cdn.example.com/user/6/uuid.jpg")
    private String thumbnail;
    @Schema(description = "복구 가능 여부", example = "true")
    private Boolean restoreEnabled;
    @Schema(description = "약관 동의 여부", example = "true")
    private Boolean isRegistered;
    @Schema(description = "사용자 고유 코드", example = "aB3jK2M8p9cR1Vw_K0Nxug")
    private String code;
    @Schema(description = "커플 여부", example = "true")
    private Boolean hasCouple;
    @Schema(description = "커플 복구 가능 여부", example = "true")
    private Boolean hasRestorableCouple;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "커플 정보", nullable = true)
    private ActiveCoupleDto couple;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "복구 가능 커플 정보", nullable = true)
    private RestorableCoupleDto restorableCouple;

    public static UserDto from(User user) {
        return UserDto.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .thumbnail(user.getThumbnail())
                .restoreEnabled(user.isRestorable())
                .isRegistered(user.hasAgreedToAllRequiredTerms())
                .code(user.getCode())
                .hasCouple(false)
                .hasRestorableCouple(false)
                .couple(null)
                .restorableCouple(null)
                .build();
    }

    public static UserDto from(User user, ActiveCoupleDto activeCoupleDto) {
        return UserDto.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .thumbnail(user.getThumbnail())
                .restoreEnabled(user.isRestorable())
                .isRegistered(user.hasAgreedToAllRequiredTerms())
                .code(user.getCode())
                .hasCouple(activeCoupleDto != null)
                .couple(activeCoupleDto)
                .hasRestorableCouple(false)
                .restorableCouple(null)
                .build();
    }

    public static UserDto from(User user, RestorableCoupleDto restorableCoupleDto) {
        return UserDto.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .thumbnail(user.getThumbnail())
                .restoreEnabled(user.isRestorable())
                .isRegistered(user.hasAgreedToAllRequiredTerms())
                .code(user.getCode())
                .hasCouple(false)
                .couple(null)
                .hasRestorableCouple(restorableCoupleDto != null)
                .restorableCouple(restorableCoupleDto)
                .build();
    }


}
