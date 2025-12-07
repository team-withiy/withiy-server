package com.server.domain.search.service;

import com.server.domain.search.dto.SearchHistoryDto;
import com.server.domain.search.entity.SearchHistory;
import com.server.domain.search.repository.SearchHistoryRepository;
import com.server.domain.user.entity.User;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SearchService {

	private final SearchHistoryRepository searchRepository;
	private static final int MAX_HISTORY_SIZE = 10;

	/**
	 * 최근 검색어 조회
	 *
	 * @param user 인증된 사용자 정보
	 * @return 최근 검색어 목록
	 */
	@Transactional
	public List<SearchHistoryDto> getRecentSearchHistory(User user) {
		// 사용자에 대한 최근 검색어 10개 조회
		Pageable pageable = PageRequest.of(0, MAX_HISTORY_SIZE);
		List<SearchHistory> searchHistories = searchRepository.findByUserIdOrderByCreatedAtDesc(
			user.getId(), pageable);

		return searchHistories.stream()
			.map(SearchHistoryDto::from)
			.collect(Collectors.toList());
	}

	public void saveSearchHistory(User user, String keyword) {
		SearchHistory searchHistory = SearchHistory.builder()
			.user(user)
			.keyword(keyword)
			.build();
		searchRepository.save(searchHistory);
	}
}
