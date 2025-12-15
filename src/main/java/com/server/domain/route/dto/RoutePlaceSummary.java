package com.server.domain.route.dto;

import com.server.domain.route.entity.RoutePlace;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoutePlaceSummary {

	private Long placeId;
	private String name;

	public static RoutePlaceSummary from(RoutePlace routePlace) {
		return RoutePlaceSummary.builder()
			.placeId(routePlace.getPlace().getId())
			.name(routePlace.getPlace().getName())
			.build();
	}
}
