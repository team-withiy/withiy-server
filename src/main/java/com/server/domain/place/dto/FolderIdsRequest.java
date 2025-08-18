package com.server.domain.place.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "폴더 ID 리스트 요청 DTO")
public class FolderIdsRequest {

	@Schema(description = "폴더 ID 리스트", example = "[1, 2, 3]")
	private Set<Long> folderIds;
}
