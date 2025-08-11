package com.server.domain.map.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.server.domain.map.dto.AddressDto;
import com.server.domain.map.dto.RoadAddressDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoordToAddressResponse {

	private Meta meta;
	private List<Document> documents;

	public static AddressDto toAddressDto(Document document, String x, String y) {
		if (document.getAddress() == null) {
			return null;
		}

		return AddressDto.builder().addressName(document.getAddress().getAddressName())
			.region1DepthName(document.getAddress().getRegion1DepthName())
			.region2DepthName(document.getAddress().getRegion2DepthName())
			.region3DepthName(document.getAddress().getRegion3DepthName())
			.mainAddressNo(document.getAddress().getMainAddressNo())
			.subAddressNo(document.getAddress().getSubAddressNo())
			.mountainYn("Y".equals(document.getAddress().getMountainYn()))
			.coordinates(com.server.domain.map.dto.CoordinateDto.builder().longitude(x)
				.latitude(y).build())
			.build();
	}

	public static RoadAddressDto toRoadAddressDto(Document document) {
		if (document.getRoadAddress() == null) {
			return null;
		}

		return RoadAddressDto.builder().addressName(document.getRoadAddress().getAddressName())
			.region1DepthName(document.getRoadAddress().getRegion1DepthName())
			.region2DepthName(document.getRoadAddress().getRegion2DepthName())
			.region3DepthName(document.getRoadAddress().getRegion3DepthName())
			.roadName(document.getRoadAddress().getRoadName())
			.mainBuildingNo(document.getRoadAddress().getMainBuildingNo())
			.subBuildingNo(document.getRoadAddress().getSubBuildingNo())
			.buildingName(document.getRoadAddress().getBuildingName())
			.zoneNo(document.getRoadAddress().getZoneNo()).build();
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Meta {

		@JsonProperty("total_count")
		private Integer totalCount;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Document {

		private Address address;
		@JsonProperty("road_address")
		private RoadAddress roadAddress;

		@Data
		@Builder
		@NoArgsConstructor
		@AllArgsConstructor
		public static class Address {

			@JsonProperty("address_name")
			private String addressName;

			@JsonProperty("region_1depth_name")
			private String region1DepthName;

			@JsonProperty("region_2depth_name")
			private String region2DepthName;

			@JsonProperty("region_3depth_name")
			private String region3DepthName;

			@JsonProperty("mountain_yn")
			private String mountainYn;

			@JsonProperty("main_address_no")
			private String mainAddressNo;

			@JsonProperty("sub_address_no")
			private String subAddressNo;
		}

		@Data
		@Builder
		@NoArgsConstructor
		@AllArgsConstructor
		public static class RoadAddress {

			@JsonProperty("address_name")
			private String addressName;

			@JsonProperty("region_1depth_name")
			private String region1DepthName;

			@JsonProperty("region_2depth_name")
			private String region2DepthName;

			@JsonProperty("region_3depth_name")
			private String region3DepthName;

			@JsonProperty("road_name")
			private String roadName;

			@JsonProperty("underground_yn")
			private String undergroundYn;

			@JsonProperty("main_building_no")
			private String mainBuildingNo;

			@JsonProperty("sub_building_no")
			private String subBuildingNo;

			@JsonProperty("building_name")
			private String buildingName;

			@JsonProperty("zone_no")
			private String zoneNo;
		}
	}
}
