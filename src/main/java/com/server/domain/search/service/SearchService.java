package com.server.domain.search.service;

import com.server.domain.search.dto.SearchHistoryDto;
import com.server.domain.search.entity.SearchHistory;
import com.server.domain.search.repository.SearchHistoryRepository;
import com.server.domain.user.entity.User;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SearchService {

	private final SearchHistoryRepository searchRepository;

	/**
	 * 최근 검색어 조회
	 *
	 * @param user 인증된 사용자 정보
	 * @return 최근 검색어 목록
	 */
	@Transactional
	public List<SearchHistoryDto> getRecentSearchHistory(User user) {
		List<SearchHistory> searchHistories = searchRepository.findTop10ByUserOrderByCreatedAtDesc(
			user);

		return searchHistories.stream()
			.map(SearchHistoryDto::from)
			.collect(Collectors.toList());
	}
}
