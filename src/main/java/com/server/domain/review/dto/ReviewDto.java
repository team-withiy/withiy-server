package com.server.domain.review.dto;

import com.server.domain.review.entity.Review;
import com.server.domain.user.dto.SimpleUserDto;
import com.server.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewDto {

	@Schema(description = "리뷰 ID", example = "12345")
	private Long reviewId;
	@Schema(description = "리뷰어 정보")
	private SimpleUserDto reviewer;
	@Schema(description = "리뷰 내용", example = "정말 좋은 장소였습니다!")
	private String contents;
	@Schema(description = "리뷰 이미지 URL 목록", example = "[\"https://example.com/image1.jpg\", \"https://example.com/image2.jpg\"]")
	private List<String> imageUrls;
	@Schema(description = "리뷰 온도 점수", example = "80")
	private Long score;
	@Schema(description = "장소 이름", example = "강남역 스타벅스")
	private String placeName;
	@Schema(description = "리뷰 생성 일시", example = "2023-10-05T14:48:00")
	private LocalDateTime updatedAt;


	@Builder
	private ReviewDto(Long reviewId, SimpleUserDto reviewer, String contents,
		List<String> imageUrls, Long score, String placeName, LocalDateTime updatedAt) {
		this.reviewId = reviewId;
		this.reviewer = reviewer;
		this.contents = contents;
		this.imageUrls = imageUrls;
		this.score = score;
		this.placeName = placeName;
		this.updatedAt = updatedAt;
	}

	public static ReviewDto of(Review review, User reviewer, List<String> imageUrls
		, String placeName) {
		return ReviewDto.builder()
			.reviewId(review.getId())
			.reviewer(SimpleUserDto.from(reviewer))
			.contents(review.getContents())
			.imageUrls(imageUrls)
			.score(review.getScore())
			.placeName(placeName)
			.updatedAt(review.getUpdatedAt())
			.build();
	}
}

