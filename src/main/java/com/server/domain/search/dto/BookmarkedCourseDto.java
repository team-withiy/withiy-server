package com.server.domain.search.dto;

import com.server.domain.route.entity.Route;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookmarkedCourseDto {

	@Schema(description = "코스 ID", example = "1")
	private Long id;
	@Schema(description = "코스 이름", example = "홍대 데이트 코스")
	private String name;

	public static BookmarkedCourseDto from(Route route) {
		return BookmarkedCourseDto.builder()
			.id(route.getId())
			.name(route.getName())
			.build();
	}
}
