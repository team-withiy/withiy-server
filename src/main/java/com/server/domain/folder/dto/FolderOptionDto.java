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
public class FolderOptionDto {

	@Schema(description = "폴더 ID", example = "1")
	private Long id;
	@Schema(description = "폴더 이름", example = "강남 맛집")
	private String name;
	@Schema(description = "폴더 색상", example = "RED")
	private FolderColor color;
	@Schema(description = "북마크 저장 개수", example = "10")
	private Long bookmarkCount;
	@Schema(description = "장소 폴더에 저장 여부", example = "true")
	private boolean isBookmarked;
	@Schema(description = "폴더 생성 시간", example = "2023-10-01T12:00:00")
	private LocalDateTime createdAt;

	@Builder
	public FolderOptionDto(Long id, String name, FolderColor color, Long bookmarkCount,
		boolean isBookmarked, LocalDateTime createdAt) {
		this.id = id;
		this.name = name;
		this.color = color;
		this.bookmarkCount = bookmarkCount;
		this.isBookmarked = isBookmarked;
		this.createdAt = createdAt;
	}

	public static FolderOptionDto from(Folder folder, Long bookmarkCount, boolean isBookmarked) {
		return FolderOptionDto.builder()
			.id(folder.getId())
			.name(folder.getName())
			.color(folder.getColor())
			.bookmarkCount(bookmarkCount)
			.isBookmarked(isBookmarked)
			.createdAt(folder.getCreatedAt())
			.build();
	}
}
