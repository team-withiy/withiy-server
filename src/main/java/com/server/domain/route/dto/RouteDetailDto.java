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
public class RouteDetailDto {

	private String name;
	private String thumbnail;
	private List<RouteImageDto> routeImageDtos;

}
