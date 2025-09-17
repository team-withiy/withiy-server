package com.server.domain.photo.repository;

import com.server.domain.photo.entity.Photo;
import com.server.domain.photo.entity.PhotoType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PhotoRepository extends JpaRepository<Photo, Long> {

	@Query("SELECT p FROM Photo p JOIN FETCH p.place WHERE p.id = :id")
	Optional<Photo> findById(Long id);

	@Query("SELECT p FROM Photo p WHERE p.place.id = :placeId AND p.type = :type")
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
	List<Photo> findAllByPlaceIdsAndType(List<Long> placeIds, PhotoType type);


	@Query("SELECT p FROM Photo p " +
		"WHERE p.place.id = :placeId " +
		"AND p.type = :type " +
		"ORDER BY p.createdAt DESC")
	List<Photo> findTopPhotosByPlaceIdAndType(Long placeId, PhotoType type, Pageable pageable);


	@Query("SELECT p FROM Photo p " +
		"WHERE p.place.id = :placeId " +
		"AND p.type = :type " +
		"AND p.id > :cursor " +
		"ORDER BY p.createdAt ASC")
	List<Photo> findPrevPhotosByPlaceIdAndType(Long placeId, PhotoType type, Long cursor,
		Pageable pageable);

	@Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
		"FROM Photo p " +
		"WHERE p.place.id = :placeId " +
		"AND p.type = :type " +
		"AND p.id < :cursor")
	boolean existsNextPhotoByPlaceIdAndType(Long placeId, PhotoType type, Long cursor);

	@Query("SELECT p FROM Photo p " +
		"WHERE p.place.id = :placeId " +
		"AND p.type = :type " +
		"AND p.id < :cursor " +
		"ORDER BY p.createdAt DESC")
	List<Photo> findNextPhotosByPlaceIdAndType(Long placeId, PhotoType type, Long cursor,
		Pageable pageable);

	@Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
		"FROM Photo p " +
		"WHERE p.place.id = :placeId " +
		"AND p.type = :type " +
		"AND p.id > :cursor")
	boolean existsPrevPhotoByPlaceIdAndType(Long placeId, PhotoType type, Long cursor);
}
