package com.server.domain.place.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LocationDto {

    private String latitude;
    private String longitude;
    private String region1depth;
    private String region2depth;
    private String region3depth;

    @Builder
    public LocationDto(String latitude, String longitude, String region1depth, String region2depth, String region3depth) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.region1depth = region1depth;
        this.region2depth = region2depth;
        this.region3depth = region3depth;
    }

}
