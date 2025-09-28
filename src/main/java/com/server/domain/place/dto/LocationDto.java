package com.server.domain.place.dto;

import com.server.domain.place.entity.Place;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LocationDto {

	private double latitude;
	private double longitude;
	private String region1depth;
	private String region2depth;
	private String region3depth;

	@Builder
	public LocationDto(double latitude, double longitude, String region1depth, String region2depth,
		String region3depth) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.region1depth = region1depth;
		this.region2depth = region2depth;
		this.region3depth = region3depth;
	}

	public static LocationDto from(Place place) {
		return LocationDto.builder()
			.latitude(place.getLatitude())
			.longitude(place.getLongitude())
			.region1depth(place.getRegion1depth())
			.region2depth(place.getRegion2depth())
			.region3depth(place.getRegion3depth())
			.build();
	}

}
