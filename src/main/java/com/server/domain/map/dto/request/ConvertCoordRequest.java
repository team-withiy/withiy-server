package com.server.domain.map.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConvertCoordRequest {
    private String x; // 변환할 x 좌표
    private String y; // 변환할 y 좌표
    private String inputCoord; // 입력 좌표계
    private String outputCoord; // 출력 좌표계
}
