package com.server.domain.review.dto;

import com.server.domain.place.dto.PlaceDto;
import com.server.domain.review.entity.Review;
import com.server.domain.user.dto.SimpleUserDto;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
public class ReviewDto {
    private Long reviewId;
    private SimpleUserDto reviewer;
    private String contents;
    private List<String> imageUrls;
    private Long score;

    @Builder
    private ReviewDto(Long reviewId, SimpleUserDto reviewer, String contents, List<String> imageUrls, Long score) {
        this.reviewId = reviewId;
        this.reviewer = reviewer;
        this.contents = contents;
        this.imageUrls = imageUrls;
        this.score = score;
    }

    public static ReviewDto from(Review review, boolean isBookmarked) {
        return ReviewDto.builder()
                .reviewId(review.getId())
                .reviewer(SimpleUserDto.from(review.getUser()))
                .contents(review.getContents())
                .score(review.getScore())
                .build();
    }
}

