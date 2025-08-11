package com.server.domain.map.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressToCoordRequest {

	private String query; // 검색할 주소
	private String analyzeType; // 검색 결과 제공 방식 (similar, exact)
}
