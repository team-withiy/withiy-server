package com.server.domain.place.dto;

import com.server.domain.place.entity.PlaceImage;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceImageDto {
    private String imageUrl;

    public static PlaceImageDto from(PlaceImage placeImage){
        return PlaceImageDto.builder()
                .imageUrl(placeImage.getImageUrl())
                .build();
    }
}
