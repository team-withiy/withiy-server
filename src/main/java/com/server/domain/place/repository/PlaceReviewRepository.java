package com.server.domain.place.repository;

import com.server.domain.place.entity.Place;
import com.server.domain.place.entity.PlaceReview;
import com.server.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceReviewRepository extends JpaRepository<PlaceReview, Long> {
    Optional<PlaceReview> findByPlaceAndUser(Place place, User user);

    @Query("SELECT pr FROM PlaceReview pr " +
        "JOIN FETCH pr.place p " +
        "JOIN FETCH pr.user u " +
        "WHERE pr.place.id IN :placeIds " +
        "ORDER BY pr.id DESC")
    List<PlaceReview> findRecentReviewsByPlaceIds(@Param("placeIds") List<Long> placeIds, Pageable pageable);
}
