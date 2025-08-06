package com.server.domain.place.dto;

import com.server.domain.photo.dto.PhotoDto;
import com.server.domain.review.dto.ReviewDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class GetPlaceDetailResponse {
    @Schema(description = "장소 ID", example = "1")
    private Long placeId;
    @Schema(description = "장소 이름", example = "맥도날드 강남점")
    private String placeName;
    @Schema(description = "장소 카테고리", example = "패스트푸드")
    private String categoryName;
    @Schema(description = "장소 주소", example = "서울특별시 강남구 테헤란로 123")
    private String address;
    @Schema(description = "장소 북마크 여부", example = "true")
    private boolean isBookmarked;
    @Schema(description = "장소 위치 정보")
    private LocationDto location;
    @Schema(description = "장소 온도 점수", example = "81")
    private Long score;
    @Schema(description = "장소 사진 목록")
    private List<PhotoDto> photos;
    @Schema(description = "장소 리뷰 목록")
    private List<ReviewDto> reviews;

    @Builder
    public GetPlaceDetailResponse(Long placeId, String placeName, String categoryName, String address,
                                  boolean isBookmarked, LocationDto location, Long score,
                                  List<PhotoDto> photos, List<ReviewDto> reviews) {
        this.placeId = placeId;
        this.placeName = placeName;
        this.categoryName = categoryName;
        this.address = address;
        this.isBookmarked = isBookmarked;
        this.location = location;
        this.score = score;
        this.photos = photos;
        this.reviews = reviews;
    }
}
