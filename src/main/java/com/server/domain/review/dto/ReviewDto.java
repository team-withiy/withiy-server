package com.server.domain.review.dto;

import com.server.domain.place.dto.PlaceDto;
import com.server.domain.review.entity.Review;
import com.server.domain.user.dto.SimpleUserDto;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewDto {
    private Long id;
    private SimpleUserDto user;
    private PlaceDto place;
    private String contents;
    private Long score;

    public static ReviewDto from(Review review, boolean isBookmarked) {

        return ReviewDto.builder()
                .id(review.getId())
                .user(SimpleUserDto.from(review.getUser()))
                .place(PlaceDto.from(review.getPlace(), isBookmarked))
                .contents(review.getContents())
                .score(review.getScore())
                .build();
    }
}

