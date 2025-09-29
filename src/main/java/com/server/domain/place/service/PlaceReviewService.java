package com.server.domain.place.service;

import com.server.domain.place.entity.Place;
import com.server.domain.place.entity.PlaceReview;
import com.server.domain.place.repository.PlaceReviewRepository;
import com.server.domain.user.entity.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceReviewService {

    private final PlaceReviewRepository placeReviewRepository;

    public Optional<PlaceReview> findByPlaceAndUser(Place place, User user) {
        return placeReviewRepository.findByPlaceAndUser(place, user);
    }

}
