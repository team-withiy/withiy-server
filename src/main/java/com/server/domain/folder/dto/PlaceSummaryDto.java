package com.server.domain.folder.dto;

import com.server.domain.place.entity.Place;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "장소 요약 정보 DTO", name = "PlaceSummaryDto")
public class PlaceSummaryDto {

	@Schema(description = "장소 ID", example = "1")
	private Long placeId;
	@Schema(description = "장소 이름", example = "스타벅스 강남점")
	private String placeName;
	@Schema(description = "장소 주소", example = "서울특별시 강남구 테헤란로 123")
	private String address;
	@Schema(description = "장소 이미지 URL 목록",
		example = "[\"https://example.com/image1.jpg\", \"https://example.com/image2.jpg\"]")
	private List<String> imageUrls;
	@Schema(description = "장소 온도 점수", example = "85")
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
