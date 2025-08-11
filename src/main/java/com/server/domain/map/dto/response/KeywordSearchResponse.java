package com.server.domain.map.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.server.domain.map.dto.CoordinateDto;
import com.server.domain.map.dto.PlaceDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class KeywordSearchResponse {

	private Meta meta;
	private List<Document> documents;

	public static PlaceDto toPlaceDto(Document document) {
		return PlaceDto.builder().id(document.getId()).placeName(document.getPlaceName())
			.categoryName(document.getCategoryName())
			.categoryGroupCode(document.getCategoryGroupCode())
			.categoryGroupName(document.getCategoryGroupName()).phone(document.getPhone())
			.addressName(document.getAddressName())
			.roadAddressName(document.getRoadAddressName()).placeUrl(document.getPlaceUrl())
			.distance(document.getDistance()).coordinates(CoordinateDto.builder()
				.longitude(document.getX()).latitude(document.getY()).build())
			.build();
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Meta {

		@JsonProperty("is_end")
		private Boolean isEnd;

		@JsonProperty("pageable_count")
		private Integer pageableCount;

		@JsonProperty("total_count")
		private Integer totalCount;

		@JsonProperty("same_name")
		private SameName sameName;

		@Data
		@Builder
		@NoArgsConstructor
		@AllArgsConstructor
		public static class SameName {

			private List<String> region;
			private String keyword;

			@JsonProperty("selected_region")
			private String selectedRegion;
		}
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Document {

		private String id; // 장소 ID

		@JsonProperty("place_name")
		private String placeName; // 장소명, 업체명

		@JsonProperty("category_name")
		private String categoryName; // 카테고리 이름

		@JsonProperty("category_group_code")
		private String categoryGroupCode; // 중요 카테고리 그룹 코드

		@JsonProperty("category_group_name")
		private String categoryGroupName; // 중요 카테고리 그룹명

		private String phone; // 전화번호

		@JsonProperty("address_name")
		private String addressName; // 전체 지번 주소

		@JsonProperty("road_address_name")
		private String roadAddressName; // 전체 도로명 주소

		@JsonProperty("x")
		private String x; // X 좌표, 경위도인 경우 경도(longitude)

		@JsonProperty("y")
		private String y; // Y 좌표, 경위도인 경우 위도(latitude)

		@JsonProperty("place_url")
		private String placeUrl; // 장소 상세페이지 URL

		private String distance; // 중심좌표까지의 거리
	}
}
