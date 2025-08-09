package com.server.domain.review.service;

import com.server.domain.place.entity.Place;
import com.server.domain.review.entity.Review;
import com.server.domain.review.repository.ReviewRepository;
import com.server.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public Review save(Place place, User user, String contents, Long score){
        Review review = new Review(place, user, contents,
                score);
        return reviewRepository.save(review);
    }

    public List<Review> getReviewsByPlace(Place place) {
        return reviewRepository.findAllByPlace(place);
    }
}
