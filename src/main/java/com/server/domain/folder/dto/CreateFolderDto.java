package com.server.domain.folder.dto;

import com.server.domain.folder.entity.FolderColor;
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
public class CreateFolderDto {

	private String name;
	private FolderColor color;

	public String getNormalizedName() {
		return name.trim().toLowerCase();
	}
}
