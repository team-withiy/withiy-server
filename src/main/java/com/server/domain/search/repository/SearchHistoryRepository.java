package com.server.domain.search.repository;

import com.server.domain.search.entity.SearchHistory;
import com.server.domain.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

	List<SearchHistory> findTop10ByUserOrderByCreatedAtDesc(User user);
}
