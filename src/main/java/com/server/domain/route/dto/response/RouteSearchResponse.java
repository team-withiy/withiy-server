package com.server.domain.route.dto.response;

import com.server.domain.route.dto.RoutePlaceSummary;
import com.server.domain.route.entity.Route;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteSearchResponse {

	private Long id;
	private String name;
	private List<RoutePlaceSummary> places;
	private List<String> images;

	public static RouteSearchResponse of(Route route, List<RoutePlaceSummary> places,
		List<String> images) {
		return RouteSearchResponse.builder()
			.id(route.getId())
			.name(route.getName())
			.places(places)
			.images(images)
			.build();
	}
}
