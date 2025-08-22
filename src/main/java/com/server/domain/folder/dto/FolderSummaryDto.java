package com.server.domain.folder.dto;

import com.server.domain.folder.entity.Folder;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "폴더 정보 DTO", name = "FolderDto")
public class FolderSummaryDto {

	@Schema(description = "폴더 ID", example = "1")
	private Long id;
	@Schema(description = "폴더 이름", example = "강남 맛집")
	private String name;
	@Schema(description = "폴더 색상", example = "#FF5733")
	private String color;
	@Schema(description = "북마크 저장 개수", example = "10")
	private Long bookmarkCount;
	@Schema(description = "폴더 썸네일 URL 목록", example = "[\"https://example.com/thumbnail1.jpg\", \"https://example.com/thumbnail2.jpg\"]")
	private List<String> thumbnails;
	@Schema(description = "폴더 생성 시간", example = "2023-10-01T12:00:00")
	private LocalDateTime createdAt;

	@Builder
	public FolderSummaryDto(Long id, String name, String color, Long bookmarkCount,
		List<String> thumbnails, LocalDateTime createdAt) {
		this.id = id;
		this.name = name;
		this.color = color;
		this.bookmarkCount = bookmarkCount;
		this.thumbnails = thumbnails;
		this.createdAt = createdAt;
	}

	public static FolderSummaryDto from(Folder folder, Long bookmarkCount,
		List<String> thumbnails) {
		return FolderSummaryDto.builder()
			.id(folder.getId())
			.name(folder.getName())
			.color(folder.getColor().getHexCode())
			.bookmarkCount(bookmarkCount)
			.thumbnails(thumbnails)
			.createdAt(folder.getCreatedAt())
			.build();
	}

	public static FolderSummaryDto from(Folder folder) {
		return FolderSummaryDto.builder()
			.id(folder.getId())
			.name(folder.getName())
			.color(folder.getColor().getHexCode())
			.bookmarkCount(0L) // 기본값 설정
			.thumbnails(List.of()) // 기본값 설정
			.createdAt(folder.getCreatedAt())
			.build();
	}
}
