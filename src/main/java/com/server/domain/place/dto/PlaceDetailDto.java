package com.server.domain.place.dto;

import com.server.domain.category.dto.CategoryDto;
import com.server.domain.photo.dto.PhotoDto;
import com.server.domain.place.entity.Place;
import com.server.domain.review.dto.ReviewDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlaceDetailDto {
    private Long id;
    private String name;
    private String address;
    private String latitude;
    private String longitude;
    private String region1depth;
    private String region2depth;
    private String region3depth;
    private CategoryDto category;
    private boolean isBookmarked;
    private Long score;

    public static PlaceDetailDto from(Place place, boolean isBookmarked) {

        return PlaceDetailDto.builder()
                .id(place.getId())
                .name(place.getName())
                .address(place.getAddress())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .region1depth(place.getRegion1depth())
                .region2depth(place.getRegion2depth())
                .region3depth(place.getRegion3depth())
                .category(CategoryDto.from(place.getCategory()))
                .isBookmarked(isBookmarked)
                .score(place.getScore())
                .build();
    }
}
