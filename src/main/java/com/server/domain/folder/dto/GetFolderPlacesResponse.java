package com.server.domain.folder.dto;

import com.server.domain.folder.entity.Folder;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetFolderPlacesResponse {

	private Long folderId;
	private String folderName;
	List<PlaceSummaryDto> places;

	@Builder
	public GetFolderPlacesResponse(Long folderId, String folderName, List<PlaceSummaryDto> places) {
		this.folderId = folderId;
		this.folderName = folderName;
		this.places = places;
	}

	public static GetFolderPlacesResponse from(Folder folder,
		List<PlaceSummaryDto> placeSummaries) {
		return GetFolderPlacesResponse.builder()
			.folderId(folder.getId())
			.folderName(folder.getName())
			.places(placeSummaries)
			.build();
	}
}
