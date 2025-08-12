package com.server.domain.folder.dto;

import com.server.domain.place.entity.Place;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PlaceSummaryDto {

	private Long placeId;
	private String placeName;
	private String address;
	private List<String> imageUrls;
	private Long score;

	@Builder
	public PlaceSummaryDto(Long placeId, String placeName, String address, List<String> imageUrls,
		Long score) {
		this.placeId = placeId;
		this.placeName = placeName;
		this.address = address;
		this.imageUrls = imageUrls;
		this.score = score;
	}

	public static PlaceSummaryDto from(Place place, List<String> imageUrls) {
		return PlaceSummaryDto.builder()
			.placeId(place.getId())
			.placeName(place.getName())
			.address(place.getAddress())
			.imageUrls(imageUrls)
			.score(place.getScore())
			.build();
	}
}
