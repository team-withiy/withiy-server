package com.server.domain.folder.dto;

import com.server.domain.folder.entity.Folder;
import com.server.domain.folder.entity.FolderColor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "폴더 정보 DTO", name = "FolderDto")
public class FolderDto {

	@Schema(description = "폴더 ID", example = "1")
	private Long id;
	@Schema(description = "폴더 이름", example = "강남 맛집")
	private String name;
	@Schema(description = "폴더 색상", example = "RED")
	private FolderColor color;
	@Schema(description = "폴더 생성 시간", example = "2023-10-01T12:00:00")
	private LocalDateTime createdAt;

	@Builder
	public FolderDto(Long id, String name, FolderColor color, LocalDateTime createdAt) {
		this.id = id;
		this.name = name;
		this.color = color;
		this.createdAt = createdAt;
	}

	public static FolderDto from(Folder folder) {
		return FolderDto.builder()
			.id(folder.getId())
			.name(folder.getName())
			.color(folder.getColor())
			.createdAt(folder.getCreatedAt())
			.build();
	}

}
