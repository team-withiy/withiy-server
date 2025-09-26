package com.server.domain.search.dto;

import com.server.domain.place.dto.PlaceDto;
import com.server.domain.route.dto.CourseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchResultResponse {

	@Schema(description = "검색된 장소 목록")
	private List<PlaceDto> searchPlaces;
	@Schema(description = "검색된 코스 목록")
	private List<CourseDto> searchCourses;
}
