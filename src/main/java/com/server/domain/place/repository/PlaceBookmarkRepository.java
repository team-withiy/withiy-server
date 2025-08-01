package com.server.domain.place.repository;

import com.server.domain.place.entity.PlaceBookmark;
import com.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PlaceBookmarkRepository extends JpaRepository<PlaceBookmark,Long> {
    boolean existsByPlaceIdAndUserId(Long placeId, Long userId);
    void deleteByUserIdAndPlaceId(Long userId, Long placeId);

    @Query("SELECT pb FROM PlaceBookmark pb JOIN FETCH pb.place WHERE pb.user = :user")
    List<PlaceBookmark> findByUserWithPlace(User user);

}
