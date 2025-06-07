package com.server.domain.review.dto;

import com.server.domain.category.dto.CategoryDto;
import com.server.domain.photo.dto.PhotoDto;
import com.server.domain.place.dto.PlaceDto;
import com.server.domain.place.entity.Place;
import com.server.domain.review.entity.Review;
import com.server.domain.user.dto.UserDto;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewDto {
    private Long id;
    private UserDto user;
    private PlaceDto place;
    private String contents;
    private Long score;

    public static ReviewDto from(Review review, boolean isBookmarked) {

        return ReviewDto.builder()
                .id(review.getId())
                .user(UserDto.from(review.getUser(),true))
                .place(PlaceDto.from(review.getPlace(), isBookmarked))
                .contents(review.getContents())
                .score(review.getScore())
                .build();
    }
}

