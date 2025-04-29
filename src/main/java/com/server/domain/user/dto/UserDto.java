package com.server.domain.user.dto;

import com.server.domain.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserDto {
    private String nickname;
    private String thumbnail;
    private Boolean restoreEnabled;
    private Boolean isRegistered;

    public static UserDto from(User user, Boolean isRegistered) {
        Boolean restoreEnabled;
        if (user.getDeletedAt() != null) {
            restoreEnabled = true;
        } else {
            restoreEnabled = false;
        }
        return UserDto.builder().nickname(user.getNickname()).thumbnail(user.getThumbnail())
                .restoreEnabled(restoreEnabled).isRegistered(isRegistered).build();
    }
}
