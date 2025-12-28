package com.server.domain.route.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PlaceDetailInRouteDto {

	private Long placeId;
	private String name;
	private CategoryInRouteDto category;
	private String address;
	private Double latitude;
	private Double longitude;
	private List<PhotoInRouteDto> photos;

}
