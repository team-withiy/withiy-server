package com.server.domain.folder.dto;

import com.server.domain.folder.entity.FolderColor;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateFolderDto {
    private String name;
    private FolderColor color;
}
