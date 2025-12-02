package com.server.domain.bookmark.dto;

import com.server.domain.photo.dto.PhotoDto;
import com.server.domain.route.entity.Route;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookmarkedCourseDto {

	@Schema(description = "코스 ID", example = "1")
	private Long id;
	@Schema(description = "코스 이름", example = "홍대 데이트 코스")
	private String name;
	@Schema(description = "코스 대표 사진 목록")
	private List<PhotoDto> photos;

	public static BookmarkedCourseDto of(Route route, List<PhotoDto> photos) {
		return BookmarkedCourseDto.builder()
			.id(route.getId())
			.name(route.getName())
			.photos(photos)
			.build();
	}
}
