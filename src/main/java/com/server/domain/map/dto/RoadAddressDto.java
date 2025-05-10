package com.server.domain.map.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoadAddressDto {
    private String addressName; // 전체 도로명 주소
    private String region1DepthName; // 시/도
    private String region2DepthName; // 구/군
    private String region3DepthName; // 동
    private String roadName; // 도로명
    private String mainBuildingNo; // 건물 본번
    private String subBuildingNo; // 건물 부번
    private String buildingName; // 건물 이름
    private String zoneNo; // 우편번호
}
