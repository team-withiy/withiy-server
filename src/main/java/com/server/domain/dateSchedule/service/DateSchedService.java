package com.server.domain.dateSchedule.service;

import com.server.domain.dateSchedule.entity.DateSchedule;
import com.server.domain.dateSchedule.repository.DateSchedRepository;
import com.server.domain.place.entity.Place;
import com.server.domain.place.repository.PlaceRepository;
import com.server.domain.route.entity.Route;
import com.server.domain.route.repository.RouteRepository;
import com.server.domain.user.entity.User;
import com.server.global.error.code.AlbumErrorCode;
import com.server.global.error.code.DateSchedErrorCode;
import com.server.global.error.code.PlaceErrorCode;
import com.server.global.error.code.RouteErrorCode;
import com.server.global.error.exception.BusinessException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DateSchedService {

	private final DateSchedRepository dateSchedRepository;
	private final PlaceRepository placeRepository;
	private final RouteRepository routeRepository;

	public DateSchedule save(DateSchedule dateSchedule) {
		return dateSchedRepository.save(dateSchedule);
	}

	public List<DateSchedule> findByScheduleAtYyyyMm(User user, String yyyyMm) {
		return dateSchedRepository.findByUserAndScheduleAtYyyyMm(user, yyyyMm);
	}

	public List<DateSchedule> findByScheduleAtYyyyMmDd(User user, String yyyyMmDd) {
		return dateSchedRepository.findByUserAndScheduleAt(user, LocalDate.parse(yyyyMmDd));
	}

	public DateSchedule findByUserAndId(User user, Long dateSchedId) {
		return dateSchedRepository.findByUserAndId(user, dateSchedId)
			.orElseThrow(() -> new BusinessException(DateSchedErrorCode.NOT_FOUND_SCHEDULE));
	}

	public DateSchedule findByUserAndAlbumId(User user, Long albumId) {
		return dateSchedRepository.findByUserAndAlbumId(user, albumId)
			.orElseThrow(() -> new BusinessException(AlbumErrorCode.NOT_FOUND_ALBUM_FOR_SCHEDULE));
	}

	public void delete(User user, Long dateSchedId) {
		DateSchedule dateSchedule = findByUserAndId(user, dateSchedId);
		dateSchedRepository.delete(dateSchedule);
	}

	/**
	 * 새로운 Route로 DateSchedule 생성
	 *
	 * @param scheduleName 일정 이름
	 * @param scheduleAt   일정 날짜
	 * @param user         사용자
	 * @param routeName    루트 이름
	 * @param placeIds     장소 ID 리스트
	 * @return 생성된 DateSchedule
	 */
	@Transactional
	public DateSchedule createDateScheduleWithNewRoute(
		String scheduleName,
		LocalDate scheduleAt,
		User user,
		String routeName,
		List<Long> placeIds
	) {
		// Place 조회
		List<Place> places = placeRepository.findAllById(placeIds);
		if (places.size() != placeIds.size()) {
			throw new BusinessException(PlaceErrorCode.NOT_FOUND);
		}

		// 데이트 일정 생성
		DateSchedule dateSchedule = DateSchedule.createWithNewRoute(
			scheduleName,
			scheduleAt,
			user,
			routeName,
			places
		);

		return dateSchedRepository.save(dateSchedule);
	}

	/**
	 * 다른 사람의 Route를 기반으로 DateSchedule 생성 (복제)
	 *
	 * @param sourceRouteId 복제할 원본 Route ID
	 * @param scheduleName  일정 이름
	 * @param scheduleAt    일정 날짜
	 * @param user          사용자
	 * @return 생성된 DateSchedule
	 */
	@Transactional
	public DateSchedule createDateScheduleFromExistingRoute(
		Long sourceRouteId,
		String scheduleName,
		LocalDate scheduleAt,
		User user
	) {
		// 원본 Route와 RoutePlace를 함께 조회 (JOIN FETCH 사용으로 N+1 문제 방지)
		Route sourceRoute = routeRepository.findByIdWithRoutePlaces(sourceRouteId)
			.orElseThrow(() -> new BusinessException(RouteErrorCode.NOT_FOUND));

		// DateSchedule 생성
		DateSchedule dateSchedule = DateSchedule.createFromExistingRoute(
			scheduleName,
			scheduleAt,
			user,
			sourceRoute
		);

		// DateSchedule 저장
		return dateSchedRepository.save(dateSchedule);
	}

	/**
	 * DateSchedule의 Route에 Place 추가
	 *
	 * @param dateSchedId   DateSchedule ID
	 * @param placeIdsToAdd 추가할 Place ID 리스트
	 * @param user          사용자
	 */
	@Transactional
	public void addPlacesToRoute(Long dateSchedId, List<Long> placeIdsToAdd, User user) {
		DateSchedule dateSchedule = findByUserAndId(user, dateSchedId);
		Route route = dateSchedule.getRoute();

		if (route == null) {
			throw new BusinessException(RouteErrorCode.NOT_FOUND);
		}

		List<Place> placesToAdd = placeRepository.findAllById(placeIdsToAdd);
		if (placesToAdd.size() != placeIdsToAdd.size()) {
			throw new BusinessException(PlaceErrorCode.NOT_FOUND);
		}

		// Route의 도메인 로직을 통해 Place 추가
		placesToAdd.forEach(route::addPlace);
	}

	/**
	 * DateSchedule의 Route에서 Place 제거
	 *
	 * @param dateSchedId      DateSchedule ID
	 * @param placeIdsToRemove 제거할 Place ID 리스트
	 * @param user             사용자
	 */
	@Transactional
	public void removePlacesFromRoute(Long dateSchedId, List<Long> placeIdsToRemove, User user) {
		DateSchedule dateSchedule = findByUserAndId(user, dateSchedId);
		Route route = dateSchedule.getRoute();

		if (route == null) {
			throw new BusinessException(RouteErrorCode.NOT_FOUND);
		}

		List<Place> placesToRemove = placeRepository.findAllById(placeIdsToRemove);
		if (placesToRemove.size() != placeIdsToRemove.size()) {
			throw new BusinessException(PlaceErrorCode.NOT_FOUND);
		}

		// Route의 도메인 로직을 통해 Place 제거
		placesToRemove.forEach(route::removePlace);
	}
}
