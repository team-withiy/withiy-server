package com.server.domain.map.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.server.domain.map.dto.RegionDto;
import com.server.domain.map.dto.CoordinateDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoordToRegionResponse {
    private Meta meta;
    private List<Document> documents;

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
        @JsonProperty("region_type")
        private String regionType; // "H"(행정동) 또는 "B"(법정동)
        
        @JsonProperty("address_name")
        private String addressName; // 전체 지역 명칭
        
        @JsonProperty("region_1depth_name")
        private String region1DepthName; // 지역 1Depth(시도)
        
        @JsonProperty("region_2depth_name")
        private String region2DepthName; // 지역 2Depth(구 단위)
        
        @JsonProperty("region_3depth_name")
        private String region3DepthName; // 지역 3Depth(동 단위)
        
        @JsonProperty("region_4depth_name")
        private String region4DepthName; // 지역 4Depth, 법정동 & 리 영역인 경우만 존재
        
        private String code; // region 코드
        private Double x; // x 좌표
        private Double y; // y 좌표
    }

    public static RegionDto toRegionDto(Document document) {
        return RegionDto.builder().regionType(document.getRegionType())
                .addressName(document.getAddressName())
                .region1DepthName(document.getRegion1DepthName())
                .region2DepthName(document.getRegion2DepthName())
                .region3DepthName(document.getRegion3DepthName())
                .region4DepthName(document.getRegion4DepthName()).code(document.getCode())
                .coordinates(CoordinateDto.builder().longitude(String.valueOf(document.getX()))
                        .latitude(String.valueOf(document.getY())).build())
                .build();
    }
}
