package com.server.domain.folder.dto;

import com.server.domain.folder.entity.Folder;
import com.server.domain.folder.entity.FolderColor;
import com.server.domain.place.dto.PlaceDto;
import com.server.domain.user.dto.UserDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FolderDto {
    private String name;
    private FolderColor color;
    private UserDto user;
    private LocalDateTime createdAt;

    public static FolderDto from(Folder folder){
        return FolderDto.builder()
                .name(folder.getName())
                .color(folder.getColor())
                .user(UserDto.from(folder.getUser(), true, null, null))
                .createdAt(folder.getCreatedAt())
                .build();
    }

}
