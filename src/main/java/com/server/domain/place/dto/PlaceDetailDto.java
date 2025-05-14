package com.server.domain.place.dto;

import com.server.domain.place.entity.Place;
import com.server.domain.place.entity.PlaceImage;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceDetailDto {
    private String name;
    private String address;
    private String latitude;
    private String longitude;
    private String categoryName;
    private Long score;
    private List<PlaceImageDto> placeImageDtos;

    public static PlaceDetailDto from(Place place){
        return PlaceDetailDto.builder()
                .name(place.getName())
                .address(place.getAddress())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .categoryName(place.getCategory().getName())
                .score(place.getScore())
                .placeImageDtos(place.getPlaceImages().stream()
                        .map(PlaceImageDto::from)
                        .collect(Collectors.toList()))
                .build();
    }
}
