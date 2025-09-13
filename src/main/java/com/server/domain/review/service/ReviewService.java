package com.server.domain.review.service;

import com.server.domain.place.entity.Place;
import com.server.domain.review.entity.Review;
import com.server.domain.review.repository.ReviewRepository;
import com.server.domain.user.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

	private final ReviewRepository reviewRepository;

	@Transactional
	public Review save(Place place, User user, String contents, Long score) {
		Review review = Review.builder()
			.place(place)
			.user(user)
			.contents(contents)
			.score(score)
			.build();

		return reviewRepository.save(review);
	}

	/**
	 * 특정 장소에 대한 리뷰를 최신순, 평점순으로 정렬하여 limit 개수만큼 조회
	 *
	 * @param place
	 * @param limit
	 * @return
	 */
	@Transactional
	public List<Review> getTopReviewsByPlace(Place place, int limit) {
		Pageable pageable = PageRequest.of(0, limit);
		return reviewRepository.findByPlaceId(place.getId(), pageable);
	}
}
