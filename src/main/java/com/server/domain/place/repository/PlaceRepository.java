package com.server.domain.place.repository;

import com.server.domain.category.entity.Category;
import com.server.domain.place.dto.PlaceStatus;
import com.server.domain.place.entity.Place;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlaceRepository extends JpaRepository<Place, Long> {

	@Query(value = "SELECT * FROM place p WHERE " +
		"CAST(p.latitude AS DECIMAL(10,8)) BETWEEN CAST(:minLat AS DECIMAL(10,8)) AND CAST(:maxLat AS DECIMAL(10,8)) AND "
		+
		"CAST(p.longitude AS DECIMAL(11,8)) BETWEEN CAST(:minLng AS DECIMAL(11,8)) AND CAST(:maxLng AS DECIMAL(11,8))",
		nativeQuery = true)
	List<Place> findByLatitudeBetweenAndLongitudeBetween(
		@Param("minLat") String minLat, @Param("maxLat") String maxLat,
		@Param("minLng") String minLng, @Param("maxLng") String maxLng);

	List<Place> findByNameContainingIgnoreCase(String keyword);

	@Query("SELECT p FROM Place p " +
		"WHERE p.category = :category " +
		"AND LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
		"AND p.status = :status")
	List<Place> findPlacesByStatusAndCategoryAndKeyword(@Param("status") PlaceStatus status,
		@Param("category") Category category, @Param("keyword") String keyword);

	@Query("SELECT p FROM Place p " +
		"WHERE p.category = :category " +
		"AND p.status = :status")
	List<Place> findPlacesByStatusAndCategory(@Param("status") PlaceStatus status,
		Category category);

	@Query("SELECT p FROM Place p WHERE p.id IN :ids")
	List<Place> findPlacesByIds(List<Long> ids, Sort sort);

	/**
	 * Haversine 공식을 이용해 각 장소와 기준 좌표 간 거리를 계산
	 *
	 * @param latitude
	 * @param longitude
	 * @param radiusKm
	 * @return
	 */
	@Query(value =
		"SELECT p.*, " +
			"       (6371 * acos(cos(radians(:lat)) * cos(radians(p.latitude)) " +
			"       * cos(radians(p.longitude) - radians(:lng)) + sin(radians(:lat)) " +
			"       * sin(radians(p.latitude)))) AS distance " +
			"FROM place p WHERE p.status = 'ACTIVE'" +
			"HAVING distance < :radius " +
			"ORDER BY distance ASC",
		nativeQuery = true)
	List<Place> findNearbyPlaces(
		@Param("lat") double latitude,
		@Param("lng") double longitude,
		@Param("radius") double radiusKm
	);
}
