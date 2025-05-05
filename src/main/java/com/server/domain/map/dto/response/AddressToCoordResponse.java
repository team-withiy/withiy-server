package com.server.domain.map.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.server.domain.map.dto.AddressDto;
import com.server.domain.map.dto.RoadAddressDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressToCoordResponse {
    private Meta meta;
    private List<Document> documents;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Meta {
        @JsonProperty("total_count")
        private Integer totalCount;

        @JsonProperty("pageable_count")
        private Integer pageableCount;

        @JsonProperty("is_end")
        private Boolean isEnd;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Document {
        @JsonProperty("address_name")
        private String addressName;

        @JsonProperty("address_type")
        private String addressType;

        private String x;
        private String y;
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

            @JsonProperty("region_3depth_h_name")
            private String region3DepthHName;

            @JsonProperty("h_code")
            private String hCode;

            @JsonProperty("b_code")
            private String bCode;

            @JsonProperty("mountain_yn")
            private String mountainYn;

            @JsonProperty("main_address_no")
            private String mainAddressNo;

            @JsonProperty("sub_address_no")
            private String subAddressNo;

            private String x;
            private String y;
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

            private String x;
            private String y;
        }
    }

    public static AddressDto toAddressDto(Document document) {
        return AddressDto.builder().addressName(document.getAddressName())
                .region1DepthName(document.getAddress().getRegion1DepthName())
                .region2DepthName(document.getAddress().getRegion2DepthName())
                .region3DepthName(document.getAddress().getRegion3DepthName())
                .mainAddressNo(document.getAddress().getMainAddressNo())
                .subAddressNo(document.getAddress().getSubAddressNo())
                .mountainYn("Y".equals(document.getAddress().getMountainYn()))
                .coordinates(com.server.domain.map.dto.CoordinateDto.builder()
                        .longitude(document.getX()).latitude(document.getY()).build())
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
}
