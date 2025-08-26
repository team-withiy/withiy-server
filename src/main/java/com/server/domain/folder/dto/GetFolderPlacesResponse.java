package com.server.domain.folder.dto;

import com.server.domain.folder.entity.Folder;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(
	description = "폴더에 포함된 장소 요약 정보 응답",
	name = "GetFolderPlacesResponse")
public class GetFolderPlacesResponse {

	@Schema(description = "폴더 ID", example = "1")
	private Long folderId;
	@Schema(description = "폴더 이름", example = "강남 맛집")
	private String folderName;
	@Schema(description = "장소 요약 정보 목록")
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

	public static GetFolderPlacesResponse ofVirtual(String folderName,
		List<PlaceSummaryDto> placeSummaries) {
		return GetFolderPlacesResponse.builder()
			.folderId(-1L) // 가상 폴더는 DB에 없으니 예약 ID (-1) 사용
			.folderName(folderName)
			.places(placeSummaries)
			.build();
	}
}
