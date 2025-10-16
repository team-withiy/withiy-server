package com.server.domain.review.repository;

import com.server.domain.review.entity.Review;
import com.server.domain.review.repository.projection.PlaceScoreProjection;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	@Query("SELECT r FROM Review r JOIN FETCH r.user u " +
		"WHERE r.place.id = :placeId " +
		"ORDER BY r.updatedAt DESC, r.score DESC")
	List<Review> findByPlaceIdOrderByUpdatedAt(Long placeId, Pageable pageable);

	@Query("SELECT r FROM Review r JOIN FETCH r.user u " +
		"WHERE r.place.id = :placeId " +
		"ORDER BY r.score DESC, r.updatedAt DESC")
	List<Review> findByPlaceIdOrderByScore(Long placeId, Pageable pageable);

	@Query("SELECT COUNT(r) FROM Review r WHERE r.place.id = :placeId")
	long countReviewsByPlaceId(Long placeId);

	// 커서 앞 페이지 조회
	@Query("SELECT r FROM Review r JOIN FETCH r.user u " +
		"WHERE r.place.id = :placeId AND " +
		"     (r.updatedAt > :cursorUpdatedAt OR " +
		"     (r.updatedAt = :cursorUpdatedAt AND r.score > :cursorScore)) " +
		"ORDER BY r.updatedAt ASC, r.score ASC")
	List<Review> findPrevReviewsByPlaceIdOrderByUpdatedAt(
		@Param("placeId") Long placeId,
		@Param("cursorUpdatedAt") LocalDateTime cursorUpdatedAt,
		@Param("cursorScore") Long cursorScore,
		Pageable pageable
	);

	@Query("SELECT r FROM Review r JOIN FETCH r.user u " +
		"WHERE r.place.id = :placeId AND " +
		"     (r.score > :cursorScore OR " +
		"     (r.score = :cursorScore AND r.updatedAt > :cursorUpdatedAt)) " +
		"ORDER BY r.score ASC, r.updatedAt ASC")
	List<Review> findPrevReviewsByPlaceIdOrderByScore(
		@Param("placeId") Long placeId,
		@Param("cursorScore") Long cursorScore,
		@Param("cursorUpdatedAt") LocalDateTime cursorUpdatedAt,
		Pageable pageable
	);

	@Query("SELECT COUNT(r) > 0 FROM Review r " +
		"WHERE r.place.id = :placeId AND " +
		"     (r.updatedAt < :cursorUpdatedAt OR " +
		"     (r.updatedAt = :cursorUpdatedAt AND r.score < :cursorScore))")
	boolean existsNextReviewByPlaceIdOrderByUpdatedAt(
		@Param("placeId") Long placeId,
		@Param("cursorUpdatedAt") LocalDateTime cursorUpdatedAt,
		@Param("cursorScore") Long cursorScore
	);

	@Query("SELECT COUNT(r) > 0 FROM Review r " +
		"WHERE r.place.id = :placeId AND " +
		"     (r.score < :cursorScore OR " +
		"     (r.score = :cursorScore AND r.updatedAt < :cursorUpdatedAt))")
	boolean existsNextReviewByPlaceIdOrderByScore(
		@Param("placeId") Long placeId,
		@Param("cursorScore") Long cursorScore,
		@Param("cursorUpdatedAt") LocalDateTime cursorUpdatedAt
	);

	@Query("SELECT r FROM Review r JOIN FETCH r.user u " +
		"WHERE r.place.id = :placeId AND " +
		"     (r.updatedAt < :cursorUpdatedAt OR " +
		"     (r.updatedAt = :cursorUpdatedAt AND r.score < :cursorScore)) " +
		"ORDER BY r.updatedAt DESC, r.score DESC")
	List<Review> findNextReviewsByPlaceIdOrderByUpdatedAt(
		@Param("placeId") Long placeId,
		@Param("cursorUpdatedAt") LocalDateTime cursorUpdatedAt,
		@Param("cursorScore") Long cursorScore,
		Pageable pageable
	);

	@Query("SELECT r FROM Review r JOIN FETCH r.user u " +
		"WHERE r.place.id = :placeId AND " +
		"     (r.score < :cursorScore OR " +
		"     (r.score = :cursorScore AND r.updatedAt < :cursorUpdatedAt)) " +
		"ORDER BY r.score DESC, r.updatedAt DESC")
	List<Review> findNextReviewsByPlaceIdOrderByScore(
		@Param("placeId") Long placeId,
		@Param("cursorScore") Long cursorScore,
		@Param("cursorUpdatedAt") LocalDateTime cursorUpdatedAt,
		Pageable pageable
	);

	@Query("SELECT COUNT(r) > 0 FROM Review r " +
		"WHERE r.place.id = :placeId AND " +
		"     (r.updatedAt > :cursorUpdatedAt OR " +
		"     (r.updatedAt = :cursorUpdatedAt AND r.score > :cursorScore))")
	boolean existsPrevReviewByPlaceIdOrderByUpdatedAt(
		@Param("placeId") Long placeId,
		@Param("cursorUpdatedAt") LocalDateTime cursorUpdatedAt,
		@Param("cursorScore") Long cursorScore
	);

	@Query("SELECT COUNT(r) > 0 FROM Review r " +
		"WHERE r.place.id = :placeId AND " +
		"     (r.score > :cursorScore OR " +
		"     (r.score = :cursorScore AND r.updatedAt > :cursorUpdatedAt))")
	boolean existsPrevReviewByPlaceIdOrderByScore(
		@Param("placeId") Long placeId,
		@Param("cursorScore") Long cursorScore,
		@Param("cursorUpdatedAt") LocalDateTime cursorUpdatedAt
	);

	@Query("SELECT r.place.id AS placeId, AVG(r.score) AS avgScore " +
		"FROM Review r WHERE r.place.id IN :placeIds GROUP BY r.place.id")
	List<PlaceScoreProjection> findAvgScoreByPlaceIds(@Param("placeIds") List<Long> placeIds);
}
