package com.server.domain.folder.dto;

import com.server.domain.folder.entity.FolderColor;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "폴더 생성 요청 DTO")
public class CreateFolderDto {

	@Schema(description = "폴더 이름", example = "맛집")
	private String name;
	@Schema(description = "폴더 색상", example = "RED")
	private FolderColor color;

	@Schema(hidden = true)
	public String getNormalizedName() {
		return name.trim().toLowerCase();
	}
}
