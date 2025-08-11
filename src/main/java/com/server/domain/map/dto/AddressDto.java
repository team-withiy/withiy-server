package com.server.domain.map.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {

	private String addressName; // 전체 주소
	private String region1DepthName; // 시/도
	private String region2DepthName; // 구/군
	private String region3DepthName; // 동/읍/면
	private String mainAddressNo; // 주소 번호
	private String subAddressNo; // 부가 주소 번호
	private boolean mountainYn; // 산 여부
	private CoordinateDto coordinates; // 좌표
}
