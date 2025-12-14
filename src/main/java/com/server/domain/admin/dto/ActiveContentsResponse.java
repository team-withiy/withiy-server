package com.server.domain.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ActiveContentsResponse {

	@Schema(description = "운영 중인 장소 목록")
	private final List<ActivePlaceDto> places;
	@Schema(description = "운영 중인 루트 목록")
	private final List<ActiveRouteDto> routes;

	@Builder
	public ActiveContentsResponse(List<ActivePlaceDto> places, List<ActiveRouteDto> routes) {
		this.places = places;
		this.routes = routes;
	}
}
