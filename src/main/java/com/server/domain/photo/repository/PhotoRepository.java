package com.server.domain.photo.repository;

import com.server.domain.photo.entity.Photo;
import com.server.domain.photo.entity.PhotoType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PhotoRepository extends JpaRepository<Photo, Long> {

	@Query("SELECT p FROM Photo p JOIN FETCH p.place WHERE p.id = :id")
	Optional<Photo> findById(Long id);

	@Query("SELECT COUNT(p) FROM Photo p " +
		"WHERE p.place.id = :placeId " +
		"AND p.type = :type")
	long countPhotosByPlaceIdAndType(Long placeId, PhotoType type);

	@Query("SELECT p.imgUrl FROM Photo p " +
		"WHERE p.place.id IN :placeIds " +
		"AND p.type = :type " +
		"ORDER BY p.createdAt DESC")
	List<String> findImageUrlsByPlaceIdsAndType(List<Long> placeIds, PhotoType type,
		Pageable pageable);

	@Query("SELECT p.imgUrl FROM Photo p " +
		"WHERE p.place.id = :placeId " +
		"AND p.type = :type " +
		"ORDER BY p.createdAt DESC")
	List<String> findImageUrlsByPlaceIdAndType(Long placeId, PhotoType type,
		Pageable pageable);

	@Query("SELECT p FROM Photo p " +
		"WHERE p.place.id IN :placeIds " +
		"AND p.type = :type " +
		"ORDER BY p.createdAt DESC")
	List<Photo> findByPlaceIdInAndType(List<Long> placeIds, PhotoType type);


	@Query("SELECT p FROM Photo p " +
		"WHERE p.place.id = :placeId " +
		"AND p.type = :type " +
		"ORDER BY p.id DESC")
	List<Photo> findPhotosByPlaceIdAndType(Long placeId, PhotoType type, Pageable pageable);

	@Query("SELECT p FROM Photo p " +
		"WHERE p.place.id = :placeId " +
		"AND p.type = :type " +
		"AND p.id < :cursor " +
		"ORDER BY p.id DESC")
	List<Photo> findNextPhotosByPlaceIdAndType(Long placeId, PhotoType type, Long cursor,
		Pageable pageable);

	@Query("SELECT p FROM Photo p " +
		"WHERE p.place.id = :placeId " +
		"AND p.type = :type " +
		"AND p.id > :cursor " +
		"ORDER BY p.id ASC")
	List<Photo> findPrevPhotosByPlaceIdAndType(Long placeId, PhotoType type, Long cursor,
		Pageable pageable);

	@Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
		"FROM Photo p " +
		"WHERE p.place.id = :placeId " +
		"AND p.type = :type " +
		"AND p.id < :cursor")
	boolean existsNextPhotoByPlaceIdAndType(Long placeId, PhotoType type, Long cursor);

	@Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
		"FROM Photo p " +
		"WHERE p.place.id = :placeId " +
		"AND p.type = :type " +
		"AND p.id > :cursor")
	boolean existsPrevPhotoByPlaceIdAndType(Long placeId, PhotoType type, Long cursor);

	@Query("SELECT p FROM Photo p " +
		"WHERE p.place.id IN :placeIds " +
		"AND p.type = :type " +
		"AND p.id IN (" +
		"  SELECT MAX(p2.id) FROM Photo p2 " +
		"  WHERE p2.place.id IN :placeIds " +
		"  AND p2.type = :type " +
		"  GROUP BY p2.place.id" +
		")")
	List<Photo> findRepresentativePhotosByPlaceIds(List<Long> placeIds, PhotoType type);

	/**
	 * 여러 장소에 대해 각 장소별로 최대 limit개의 사진을 조회 (PostgreSQL 윈도우 함수 사용)
	 * ROW_NUMBER()를 사용하여 DB 레벨에서 제한하므로 메모리 효율적
	 *
	 * @param placeIds 장소 ID 목록
	 * @param type 사진 타입 (EnumType.STRING이므로 문자열로 비교)
	 * @param limit 각 장소별 최대 사진 개수
	 * @return 장소별로 제한된 사진 목록
	 */
	@Query(value = "SELECT p.* FROM ( " +
		"  SELECT p.*, ROW_NUMBER() OVER(PARTITION BY p.place_id ORDER BY p.created_at DESC) as rn " +
		"  FROM photo p " +
		"  WHERE p.place_id IN :placeIds AND p.type = :type " +
		") p " +
		"WHERE p.rn <= :limit", nativeQuery = true)
	List<Photo> findLimitedPhotosPerPlace(@Param("placeIds") List<Long> placeIds,
		@Param("type") String type,
		@Param("limit") int limit);
}
