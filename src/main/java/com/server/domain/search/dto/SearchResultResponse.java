package com.server.domain.search.dto;

import com.server.domain.place.dto.PlaceDto;
import com.server.domain.route.dto.RouteDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SearchResultResponse {

	@Schema(description = "검색된 장소 목록")
	private List<PlaceDto> searchPlaces;
	@Schema(description = "검색된 코스 목록")
	private List<RouteDto> searchRoutes;
}
