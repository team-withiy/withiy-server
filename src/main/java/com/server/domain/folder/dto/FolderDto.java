package com.server.domain.folder.dto;

import com.server.domain.folder.entity.Folder;
import com.server.domain.folder.entity.FolderColor;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FolderDto {

	private Long id;
	private String name;
	private FolderColor color;
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
