package com.server.domain.place.dto;

import com.server.domain.place.entity.PlaceImage;
import lombok.*;

import java.util.List;

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
}
