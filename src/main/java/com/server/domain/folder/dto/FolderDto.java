package com.server.domain.folder.dto;

import com.server.domain.folder.entity.Folder;
import com.server.domain.folder.entity.FolderColor;
import com.server.domain.user.dto.SimpleUserDto;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FolderDto {

	private String name;
	private FolderColor color;
	private SimpleUserDto user;
	private LocalDateTime createdAt;

	@Builder
	public FolderDto(String name, FolderColor color, SimpleUserDto user, LocalDateTime createdAt) {
		this.name = name;
		this.color = color;
		this.user = user;
		this.createdAt = createdAt;
	}

	public static FolderDto from(Folder folder) {
		return FolderDto.builder()
			.name(folder.getName())
			.color(folder.getColor())
			.user(SimpleUserDto.from(folder.getUser()))
			.createdAt(folder.getCreatedAt())
			.build();
	}

}
