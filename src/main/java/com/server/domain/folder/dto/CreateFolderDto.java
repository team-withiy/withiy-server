package com.server.domain.folder.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
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
	@Schema(description = "폴더 색상", example = "#FF5733")
	@Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color must be a valid hex code (e.g., #FF5733)")
	private String color;

	@Schema(hidden = true)
	public String getNormalizedName() {
		return name.trim().toLowerCase();
	}
}
