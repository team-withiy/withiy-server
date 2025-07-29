package com.server.domain.folder.dto;

import com.server.domain.folder.entity.Folder;
import com.server.domain.folder.entity.FolderColor;
import com.server.domain.user.dto.SimpleUserDto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FolderDto {
    private String name;
    private FolderColor color;
    private SimpleUserDto user;
    private LocalDateTime createdAt;

    public static FolderDto from(Folder folder){
        return FolderDto.builder()
                .name(folder.getName())
                .color(folder.getColor())
                .user(SimpleUserDto.from(folder.getUser()))
                .createdAt(folder.getCreatedAt())
                .build();
    }

}
