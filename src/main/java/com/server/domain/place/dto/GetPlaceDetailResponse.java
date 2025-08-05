package com.server.domain.place.dto;

import com.server.domain.photo.dto.PhotoDto;
import com.server.domain.review.dto.ReviewDto;
import lombok.Getter;

import java.util.List;

@Getter
public class GetPlaceDetailResponse {
    private Long placeId;
    private String placeName;
    private String categoryName;
    private String address;
    private boolean isBookmarked;
    private LocationDto location;
    private Long score;
    private List<PhotoDto> photos;
    private List<ReviewDto> reviews;
}
