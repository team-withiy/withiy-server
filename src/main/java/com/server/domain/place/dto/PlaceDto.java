package com.server.domain.place.dto;

import com.server.domain.place.entity.Place;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceDto {
    private String name;
    private String address;
    private String latitude;
    private String longitude;
    private String categoryName;

    public static PlaceDto from(Place place){
        return PlaceDto.builder()
                .name(place.getName())
                .address(place.getAddress())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .categoryName(place.getCategory().getName())
                .build();
    }
}
