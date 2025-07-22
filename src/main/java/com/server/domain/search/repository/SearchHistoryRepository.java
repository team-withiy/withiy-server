package com.server.domain.search.repository;

import com.server.domain.search.entity.SearchHistory;
import com.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    List<SearchHistory> findTop10ByUserOrderByCreatedAtDesc(User user);
}
