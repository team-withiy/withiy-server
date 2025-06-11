package com.server.domain.place.dto;

import com.server.domain.category.dto.CategoryDto;
import com.server.domain.photo.dto.PhotoDto;
import com.server.domain.place.entity.Place;
import com.server.domain.review.dto.ReviewDto;
import com.server.global.config.S3UrlConfig;
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
    private List<PhotoDto> photos;
    private List<ReviewDto> reviews;

    public static PlaceDetailDto from(Place place, boolean isBookmarked, S3UrlConfig s3UrlConfig) {

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
                .photos(place.getPhotos().stream().map(PhotoDto::from).toList())
                .reviews(place.getReviews().stream().map(review -> ReviewDto.from(review, isBookmarked, s3UrlConfig)).toList())
                .build();
    }
}
