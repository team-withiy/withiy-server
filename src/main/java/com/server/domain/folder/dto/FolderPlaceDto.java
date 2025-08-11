package com.server.domain.folder.dto;

import com.server.domain.place.dto.PlaceDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FolderPlaceDto {

	private FolderDto folder;
	private PlaceDto place;


}
