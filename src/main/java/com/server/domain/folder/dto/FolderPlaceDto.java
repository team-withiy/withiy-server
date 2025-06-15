package com.server.domain.folder.dto;

import com.server.domain.folder.entity.FolderPlace;
import com.server.domain.place.dto.PlaceDto;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FolderPlaceDto {
    private FolderDto folder;
    private PlaceDto place;


}
