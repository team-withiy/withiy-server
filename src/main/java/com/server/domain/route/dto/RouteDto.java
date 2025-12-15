package com.server.domain.route.dto;

import com.server.domain.photo.dto.PhotoSummary;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteDto {

	private Long id;
	private String name;
	private List<RoutePlaceSummary> places;
	private List<PhotoSummary> photos;
	private boolean bookmarked;
}
