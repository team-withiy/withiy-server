package com.server.domain.map.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoordToAddressRequest {
    private String x; // 경도(longitude)
    private String y; // 위도(latitude)
    private String inputCoord; // 입력 좌표계
}
