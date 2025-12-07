package com.server.domain.search.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.server.domain.place.dto.PlaceDto;
import com.server.domain.route.dto.response.RouteSearchResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class SearchResultResponse {

	@Schema(description = "검색된 장소 목록")
	private List<PlaceDto> searchPlaces;
	@Schema(description = "검색된 코스 목록")
	private List<RouteSearchResponse> searchCourses;
}
