package com.server.domain.review.repository;

import com.server.domain.review.entity.Review;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	
	@Query("SELECT r FROM Review r JOIN FETCH r.user u " +
		"WHERE r.place.id = :placeId " +
		"ORDER BY r.updatedAt DESC, r.score DESC")
	List<Review> findByPlaceId(Long placeId, Pageable pageable);
}
