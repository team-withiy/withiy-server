package com.server.domain.place.dto;

import com.server.domain.category.dto.CategoryDto;
import com.server.domain.photo.dto.PhotoDto;
import com.server.domain.review.dto.ReviewDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetPlaceDetailResponse {

	@Schema(description = "장소 ID", example = "1")
	private Long placeId;
	@Schema(description = "장소 이름", example = "맥도날드 강남점")
	private String placeName;
	@Schema(description = "장소 카테고리 이름")
	private CategoryDto category;
	@Schema(description = "장소 주소", example = "서울특별시 강남구 테헤란로 123")
	private String address;
	@Schema(description = "장소 위치 정보")
	private LocationDto location;
	@Schema(description = "장소 온도 점수", example = "81")
	private Long score;
	@Schema(description = "장소 사진 목록")
	private List<PhotoDto> photos;
	@Schema(description = "장소 사진 총 개수", example = "100")
	private long totalPhotoCount;
	@Schema(description = "장소 리뷰 목록")
	private List<ReviewDto> reviews;

	@Builder
	public GetPlaceDetailResponse(Long placeId, String placeName, CategoryDto category,
		String address, LocationDto location, Long score, List<PhotoDto> photos,
		long totalPhotoCount, List<ReviewDto> reviews) {
		this.placeId = placeId;
		this.placeName = placeName;
		this.category = category;
		this.address = address;
		this.location = location;
		this.score = score;
		this.photos = photos;
		this.totalPhotoCount = totalPhotoCount;
		this.reviews = reviews;
	}
}
