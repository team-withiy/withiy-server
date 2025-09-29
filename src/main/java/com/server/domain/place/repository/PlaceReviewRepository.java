package com.server.domain.place.repository;

import com.server.domain.place.entity.Place;
import com.server.domain.place.entity.PlaceReview;
import com.server.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceReviewRepository extends JpaRepository<PlaceReview, Long> {
    Optional<PlaceReview> findByPlaceAndUser(Place place, User user);
}
