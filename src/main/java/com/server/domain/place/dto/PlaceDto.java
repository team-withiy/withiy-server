package com.server.domain.place.dto;

import com.server.domain.category.dto.CategoryDto;
import com.server.domain.place.entity.Place;
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
	private String latitude;
	private String longitude;
	private String region1depth;
	private String region2depth;
	private String region3depth;
	private CategoryDto category;
	private boolean isBookmarked;
	private Long score;


	public static PlaceDto from(Place place, boolean isBookmarked) {

		return PlaceDto.builder()
			.id(place.getId())
			.name(place.getName())
			.address(place.getAddress())
			.latitude(place.getLatitude())
			.longitude(place.getLongitude())
			.region1depth(place.getRegion1depth())
			.region2depth(place.getRegion2depth())
			.region3depth(place.getRegion3depth())
			.category(CategoryDto.from(place.getCategory()))
			.isBookmarked(isBookmarked)
			.score(place.getScore())
			.build();
	}

	public static PlaceDto from(Place place) {

		return PlaceDto.builder()
			.id(place.getId())
			.name(place.getName())
			.address(place.getAddress())
			.latitude(place.getLatitude())
			.longitude(place.getLongitude())
			.region1depth(place.getRegion1depth())
			.region2depth(place.getRegion2depth())
			.region3depth(place.getRegion3depth())
			.category(CategoryDto.from(place.getCategory()))
			.score(place.getScore())
			.build();
	}

}
