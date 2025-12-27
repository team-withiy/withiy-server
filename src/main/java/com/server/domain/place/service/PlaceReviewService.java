package com.server.domain.place.service;

import com.server.domain.place.entity.Place;
import com.server.domain.place.entity.PlaceReview;
import com.server.domain.place.repository.PlaceReviewRepository;
import com.server.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceReviewService {

	private final PlaceReviewRepository placeReviewRepository;

	public Optional<PlaceReview> findByPlaceAndUser(Place place, User user) {
		return placeReviewRepository.findByPlaceAndUser(place, user);
	}

	/**
	 * 여러 장소의 최신 리뷰 조회
	 *
	 * @param placeIds 장소 ID 목록
	 * @param limit    최대 리뷰 개수
	 * @return 리뷰 목록
	 */
	@Transactional(readOnly = true)
	public List<PlaceReview> getRecentReviewsByPlaceIds(List<Long> placeIds, int limit) {
		return placeReviewRepository.findRecentReviewsByPlaceIds(
			placeIds, PageRequest.of(0, limit));
	}
}
