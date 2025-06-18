package com.server.domain.place.repository;

import com.server.domain.place.entity.Place;
import com.server.domain.place.entity.PlaceBookmark;
import com.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceBookmarkRepository extends JpaRepository<PlaceBookmark,Long> {
    boolean existsByPlaceIdAndUserId(Long placeId, Long userId);
    void deleteByUserIdAndPlaceId(Long userId, Long placeId);

}
