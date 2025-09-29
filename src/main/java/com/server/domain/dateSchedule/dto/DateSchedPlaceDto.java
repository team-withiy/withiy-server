package com.server.domain.dateSchedule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DateSchedPlaceDto {

    @NotEmpty(message = "name : 장소명은 필수 입니다.")
    @Schema(description = "장소 이름", example = "서울숲")
    private String name;

    @NotEmpty(message = "address : 주소는 필수 입니다.")
    @Schema(description = "장소 주소", example = "서울특별시 성동구 서울숲길 273")
    private String address;

    @NotEmpty(message = "region1depth : 지역은 필수 입니다.")
    @Schema(description = "장소 지역 1단계", example = "서울특별시")
    private String region1depth;

    @NotEmpty(message = "region2depth : 지역은 필수 입니다.")
    @Schema(description = "장소 지역 2단계", example = "성동구")
    private String region2depth;

    @NotEmpty(message = "region3depth : 지역은 필수 입니다.")
    @Schema(description = "장소 지역 3단계", example = "서울숲동")
    private String region3depth;

    @NotEmpty(message = "latitude : 위도는 필수 입니다.")
    @Schema(description = "장소 위도", example = "37.5432")
    private Double latitude;

    @NotEmpty(message = "longitude : 경도는 필수 입니다.")
    @Schema(description = "장소 경도", example = "127.0423")
    private Double longitude;
}
