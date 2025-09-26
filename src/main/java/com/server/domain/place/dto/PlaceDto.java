package com.server.domain.place.dto;

import com.server.domain.category.dto.CategoryDto;
import com.server.domain.place.entity.Place;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlaceDto {

	private Long id;
	private String name;
	private String address;
	private LocationDto location;
	private CategoryDto category;
	private boolean bookmarked;
	private Double score;
	private List<String> photoUrls;

	public static PlaceDto from(Place place) {
		return PlaceDto.builder()
			.id(place.getId())
			.name(place.getName())
			.address(place.getAddress())
			.location(LocationDto.from(place))
			.category(CategoryDto.from(place.getCategory()))
			.build();
	}

	public static PlaceDto from(Place place, boolean bookmarked, Double score,
		List<String> photoUrls) {
		return PlaceDto.builder()
			.id(place.getId())
			.name(place.getName())
			.address(place.getAddress())
			.location(LocationDto.from(place))
			.category(CategoryDto.from(place.getCategory()))
			.bookmarked(bookmarked)
			.score(score != null ? score : 0.0)
			.photoUrls(photoUrls != null ? photoUrls : Collections.emptyList())
			.build();
	}
}
