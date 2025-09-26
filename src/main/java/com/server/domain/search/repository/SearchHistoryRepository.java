package com.server.domain.search.repository;

import com.server.domain.search.entity.SearchHistory;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

	@Query("SELECT sh FROM SearchHistory sh WHERE sh.user.id = :userId ORDER BY sh.createdAt DESC")
	List<SearchHistory> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId,
		Pageable pageable);
}
