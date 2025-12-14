package com.server.domain.review.repository;

import com.server.domain.review.entity.Review;
import com.server.domain.review.repository.projection.PlaceScoreProjection;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	// ========== Latest 정렬 (최근 생성순 = ID DESC) ==========

	@Query("SELECT r FROM Review r JOIN FETCH r.user u " +
		"WHERE r.place.id = :placeId " +
		"ORDER BY r.id DESC")
	List<Review> findByPlaceIdOrderByLatest(Long placeId, Pageable pageable);

	@Query("SELECT r FROM Review r JOIN FETCH r.user u " +
		"WHERE r.place.id = :placeId AND r.id < :cursor " +
		"ORDER BY r.id DESC")
	List<Review> findNextByPlaceIdOrderByLatest(
		@Param("placeId") Long placeId,
		@Param("cursor") Long cursor,
		Pageable pageable
	);

	@Query("SELECT r FROM Review r JOIN FETCH r.user u " +
		"WHERE r.place.id = :placeId AND r.id > :cursor " +
		"ORDER BY r.id ASC")
	List<Review> findPrevByPlaceIdOrderByLatest(
		@Param("placeId") Long placeId,
		@Param("cursor") Long cursor,
		Pageable pageable
	);

	@Query("SELECT COUNT(r) > 0 FROM Review r " +
		"WHERE r.place.id = :placeId AND r.id < :cursor")
	boolean existsNextByPlaceIdOrderByLatest(
		@Param("placeId") Long placeId,
		@Param("cursor") Long cursor
	);

	@Query("SELECT COUNT(r) > 0 FROM Review r " +
		"WHERE r.place.id = :placeId AND r.id > :cursor")
	boolean existsPrevByPlaceIdOrderByLatest(
		@Param("placeId") Long placeId,
		@Param("cursor") Long cursor
	);

	// ========== Score 정렬 (평점순 = score DESC, id DESC) ==========

	@Query("SELECT r FROM Review r JOIN FETCH r.user u " +
		"WHERE r.place.id = :placeId " +
		"ORDER BY r.score DESC, r.id DESC")
	List<Review> findByPlaceIdOrderByScore(Long placeId, Pageable pageable);

	@Query("SELECT r FROM Review r JOIN FETCH r.user u " +
		"WHERE r.place.id = :placeId AND " +
		"     (r.score < :cursorScore OR (r.score = :cursorScore AND r.id < :cursorId)) " +
		"ORDER BY r.score DESC, r.id DESC")
	List<Review> findNextByPlaceIdOrderByScore(
		@Param("placeId") Long placeId,
		@Param("cursorScore") Long cursorScore,
		@Param("cursorId") Long cursorId,
		Pageable pageable
	);

	@Query("SELECT r FROM Review r JOIN FETCH r.user u " +
		"WHERE r.place.id = :placeId AND " +
		"     (r.score > :cursorScore OR (r.score = :cursorScore AND r.id > :cursorId)) " +
		"ORDER BY r.score ASC, r.id ASC")
	List<Review> findPrevByPlaceIdOrderByScore(
		@Param("placeId") Long placeId,
		@Param("cursorScore") Long cursorScore,
		@Param("cursorId") Long cursorId,
		Pageable pageable
	);

	@Query("SELECT COUNT(r) > 0 FROM Review r " +
		"WHERE r.place.id = :placeId AND " +
		"     (r.score < :cursorScore OR (r.score = :cursorScore AND r.id < :cursorId))")
	boolean existsNextByPlaceIdOrderByScore(
		@Param("placeId") Long placeId,
		@Param("cursorScore") Long cursorScore,
		@Param("cursorId") Long cursorId
	);

	@Query("SELECT COUNT(r) > 0 FROM Review r " +
		"WHERE r.place.id = :placeId AND " +
		"     (r.score > :cursorScore OR (r.score = :cursorScore AND r.id > :cursorId))")
	boolean existsPrevByPlaceIdOrderByScore(
		@Param("placeId") Long placeId,
		@Param("cursorScore") Long cursorScore,
		@Param("cursorId") Long cursorId
	);

	// ========== 공통 ==========

	@Query("SELECT COUNT(r) FROM Review r WHERE r.place.id = :placeId")
	long countReviewsByPlaceId(Long placeId);

	@Query("SELECT r.place.id AS placeId, AVG(r.score) AS avgScore " +
		"FROM Review r WHERE r.place.id IN :placeIds GROUP BY r.place.id")
	List<PlaceScoreProjection> findAvgScoreByPlaceIds(@Param("placeIds") List<Long> placeIds);
}
