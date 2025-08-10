package com.server.domain.review.dto;

import com.server.domain.review.entity.Review;
import com.server.domain.user.dto.SimpleUserDto;
import com.server.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
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

	@Builder
	private ReviewDto(Long reviewId, SimpleUserDto reviewer, String contents,
		List<String> imageUrls, Long score) {
		this.reviewId = reviewId;
		this.reviewer = reviewer;
		this.contents = contents;
		this.imageUrls = imageUrls;
		this.score = score;
	}

	public static ReviewDto of(Review review, User reviewer, List<String> imageUrls) {
		return ReviewDto.builder()
			.reviewId(review.getId())
			.reviewer(SimpleUserDto.from(reviewer))
			.contents(review.getContents())
			.imageUrls(imageUrls)
			.score(review.getScore())
			.build();
	}
}

