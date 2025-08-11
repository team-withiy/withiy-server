package com.server.domain.review.repository;

import com.server.domain.place.entity.Place;
import com.server.domain.review.entity.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	List<Review> findAllByPlace(Place place);
}
