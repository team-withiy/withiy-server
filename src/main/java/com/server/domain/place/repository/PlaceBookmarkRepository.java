package com.server.domain.place.repository;

import com.server.domain.place.entity.Place;
import com.server.domain.place.entity.PlaceBookmark;
import com.server.domain.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlaceBookmarkRepository extends JpaRepository<PlaceBookmark, Long> {

	boolean existsByPlaceIdAndUserId(Long placeId, Long userId);

	void deleteByUserIdAndPlaceId(Long userId, Long placeId);

	@Query("SELECT pb FROM PlaceBookmark pb JOIN FETCH pb.place WHERE pb.user = :user")
	List<PlaceBookmark> findByUserWithPlace(User user);

	@Query("SELECT COUNT(pb) FROM PlaceBookmark pb " +
		"WHERE pb.place = :place AND pb.deletedAt IS NULL")
	long countByPlaceAndNotDeleted(@Param("place") Place place);

	boolean existsByPlaceAndUser(Place place, User user);
}
