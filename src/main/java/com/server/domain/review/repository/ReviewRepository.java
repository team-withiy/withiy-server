package com.server.domain.review.repository;

import com.server.domain.review.entity.Review;
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
	List<Review> findByPlaceId(Long placeId, Pageable pageable);

	@Query("SELECT COUNT(r) FROM Review r WHERE r.place.id = :placeId")
	long countReviewsByPlaceId(Long placeId);

	// 커서 앞 페이지 조회
	@Query("SELECT r FROM Review r JOIN FETCH r.user u " +
		"WHERE r.place.id = :placeId AND " +
		"     (r.updatedAt > :cursorUpdatedAt OR " +
		"     (r.updatedAt = :cursorUpdatedAt AND r.score > :cursorScore)) " +
		"ORDER BY r.updatedAt ASC, r.score ASC")
	List<Review> findPrevReviewsByPlaceId(@Param("placeId") Long placeId,
		@Param("cursorUpdatedAt") LocalDateTime cursorUpdatedAt,
		@Param("cursorScore") Long cursorScore,
		Pageable pageable);

	@Query("SELECT COUNT(r) > 0 FROM Review r " +
		"WHERE r.place.id = :placeId AND " +
		"     (r.updatedAt < :cursorUpdatedAt OR " +
		"     (r.updatedAt = :cursorUpdatedAt AND r.score < :cursorScore))")
	boolean existsNextReviewByPlaceId(@Param("placeId") Long placeId,
		@Param("cursorUpdatedAt") LocalDateTime cursorUpdatedAt,
		@Param("cursorScore") Long cursorScore);

	@Query("SELECT r FROM Review r JOIN FETCH r.user u " +
		"WHERE r.place.id = :placeId AND " +
		"     (r.updatedAt < :cursorUpdatedAt OR " +
		"     (r.updatedAt = :cursorUpdatedAt AND r.score < :cursorScore)) " +
		"ORDER BY r.updatedAt DESC, r.score DESC")
	List<Review> findNextReviewsByPlaceId(@Param("placeId") Long placeId,
		@Param("cursorUpdatedAt") LocalDateTime cursorUpdatedAt,
		@Param("cursorScore") Long cursorScore,
		Pageable pageable);

	@Query("SELECT COUNT(r) > 0 FROM Review r " +
		"WHERE r.place.id = :placeId AND " +
		"     (r.updatedAt > :cursorUpdatedAt OR " +
		"     (r.updatedAt = :cursorUpdatedAt AND r.score > :cursorScore))")
	boolean existsPrevReviewByPlaceId(@Param("placeId") Long placeId,
		@Param("cursorUpdatedAt") LocalDateTime cursorUpdatedAt,
		@Param("cursorScore") Long cursorScore);
}
