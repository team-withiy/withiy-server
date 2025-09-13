package com.server.domain.review.service;


import com.server.domain.place.entity.Place;
import com.server.domain.place.service.PlaceService;
import com.server.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewFacade {

	private final ReviewService reviewService;
	private final PlaceService placeService;

	public void createReview(Long placeId, User user, String contents, Long score) {
		Place place = placeService.getPlaceById(placeId);
		reviewService.save(place, user, contents, score);
	}

}
