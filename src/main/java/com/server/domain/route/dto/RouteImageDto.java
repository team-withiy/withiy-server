package com.server.domain.route.dto;

import com.server.domain.route.entity.RouteImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class RouteImageDto {

	private String imageUrl;

	public static RouteImageDto from(RouteImage routeImage) {
		return RouteImageDto.builder()
			.imageUrl(routeImage.getImageUrl())
			.build();
	}
}
