package com.server.domain.folder.dto;

import com.server.domain.folder.entity.FolderColor;
import com.server.domain.user.dto.UserDto;
import lombok.*;
import org.joda.time.LocalDateTime;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateFolderDto {
    private String name;
    private FolderColor color;

    public String getNormalizedName() {
        return name.trim().toLowerCase();
    }
}
