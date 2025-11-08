package com.server.domain.dateSchedule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DateSchedPlaceDto {

    @Schema(description = "장소 ID", example = "1")
    private Long placeId;

    @Schema(description = "장소 이름", example = "서울숲")
    private String name;

    @Schema(description = "장소 주소", example = "서울특별시 성동구 서울숲길 273")
    private String address;

    @Schema(description = "장소 지역 1단계", example = "서울특별시")
    private String region1depth;

    @Schema(description = "장소 지역 2단계", example = "성동구")
    private String region2depth;

    @Schema(description = "장소 지역 3단계", example = "서울숲동")
    private String region3depth;

    @Schema(description = "장소 위도", example = "37.5432")
    private String latitude;

    @Schema(description = "장소 경도", example = "127.0423")
    private String longitude;
}
