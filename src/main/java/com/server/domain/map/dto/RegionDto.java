package com.server.domain.map.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegionDto {

	private String regionType; // 지역 타입 (H: 행정동, B: 법정동)
	private String addressName; // 전체 지역 명칭
	private String region1DepthName; // 시/도
	private String region2DepthName; // 구/군
	private String region3DepthName; // 동/읍/면
	private String region4DepthName; // 리
	private String code; // 지역 코드
	private CoordinateDto coordinates; // 좌표
}
