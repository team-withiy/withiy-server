package com.server.domain.place.service;

import com.server.domain.category.entity.Category;
import com.server.domain.category.service.CategoryService;
import com.server.domain.folder.repository.FolderPlaceRepository;
import com.server.domain.place.dto.PlaceDetailDto;
import com.server.domain.place.dto.PlaceDto;
import com.server.domain.place.dto.UpdatePlaceDto;
import com.server.domain.place.entity.Place;
import com.server.domain.place.entity.PlaceStatus;
import com.server.domain.place.executor.PlaceCursorQueryExecutor;
import com.server.domain.place.repository.PlaceRepository;
import com.server.global.error.code.PlaceErrorCode;
import com.server.global.error.exception.BusinessException;
import com.server.global.pagination.dto.ApiCursorPaginationRequest;
import com.server.global.pagination.dto.CursorPageDto;
import com.server.global.pagination.executor.CursorQueryExecutor;
import com.server.global.pagination.service.PaginationService;
import com.server.global.pagination.strategy.PaginationContext;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlaceService {

	private final PlaceRepository placeRepository;
	private final FolderPlaceRepository folderPlaceRepository;
	private final CategoryService categoryService;
	private final PaginationService paginationService;

	public Place save(Place place) {
		return placeRepository.save(place);
	}

	public Place getPlaceById(Long placeId) {
		Place place = placeRepository.findByPlaceId(placeId)
			.orElseThrow(() -> {
				System.out.println("@@@@");
				log.warn("Place not found. id={}", placeId);  // ← 여긴 반드시 찍힘
				return new BusinessException(PlaceErrorCode.NOT_FOUND);
			});

		System.out.println("!!!");
		log.info("place : {} ", place);
		return place;
	}

	@Transactional
	public PlaceDto getPlaceSimpleDetail(Long placeId) {
		Place place = placeRepository.findById(placeId)
			.orElseThrow(() -> new BusinessException(PlaceErrorCode.NOT_FOUND));

		return PlaceDto.from(place);
	}

	@Transactional
	public PlaceDetailDto updatePlace(Long placeId, UpdatePlaceDto updatePlaceDto) {
		Place place = placeRepository.findById(placeId)
			.orElseThrow(() -> new BusinessException(PlaceErrorCode.NOT_FOUND));

		Category category = categoryService.getCategoryByName(updatePlaceDto.getCategory());
		place.update(updatePlaceDto, category);
		return PlaceDetailDto.from(place, false);
	}

	@Transactional
	public String deletePlace(Long placeId) {
		Place place = placeRepository.findById(placeId)
			.orElseThrow(() -> new BusinessException(PlaceErrorCode.NOT_FOUND));
		String result = place.getName();
		placeRepository.delete(place);
		return result + " delete.";
	}

	/**
	 * 키워드로 장소를 검색합니다.
	 *
	 * @param keyword 검색 키워드
	 * @return 검색된 장소 목록
	 */
	@Transactional
	public List<PlaceDto> searchByKeyword(String keyword) {

		List<Place> places = placeRepository.findByNameContainingIgnoreCase(keyword);

		return places.stream()
			.map(PlaceDto::from)
			.collect(Collectors.toList());
	}

	public List<Place> getActivePlacesByCategoryAndKeyword(Category category, String keyword) {
		if (keyword == null || keyword.isBlank()) {
			return placeRepository.findPlacesByStatusAndCategory(PlaceStatus.ACTIVE, category);
		}
		return placeRepository.findPlacesByStatusAndCategoryAndKeyword(PlaceStatus.ACTIVE, category,
			keyword);
	}

	/**
	 * 커서 기반 페이징으로 폴더의 장소 조회
	 * 
	 * <p>ID 기준으로 정렬하여 조회합니다.
	 *
	 * @param folderId    폴더 ID
	 * @param pageRequest 페이징 요청
	 * @return 페이징된 장소 목록
	 */
	@Transactional(readOnly = true)
	public CursorPageDto<Place, Long> getPlacesByFolder(
		Long folderId,
		ApiCursorPaginationRequest pageRequest) {

		// 1. Executor 생성 (특정 폴더)
		CursorQueryExecutor<Place, Long> executor = 
			new PlaceCursorQueryExecutor(
				placeRepository, 
				folderPlaceRepository, 
				folderId, 
				null
			);

		// 2. Context 구성
		PaginationContext<Place, Long> context = PaginationContext.<Place, Long>builder()
			.request(pageRequest)
			.queryExecutor(executor)
			.idExtractor(Place::getId)
			.build();

		// 3. 페이징 실행
		return paginationService.paginate(context);
	}

	/**
	 * 커서 기반 페이징으로 사용자의 전체 폴더 장소 조회
	 *
	 * @param userId      사용자 ID
	 * @param pageRequest 페이징 요청
	 * @return 페이징된 장소 목록
	 */
	@Transactional(readOnly = true)
	public CursorPageDto<Place, Long> getAllPlacesInFolders(
		Long userId,
		ApiCursorPaginationRequest pageRequest) {

		// 1. Executor 생성 (전체 폴더)
		CursorQueryExecutor<Place, Long> executor = 
			new PlaceCursorQueryExecutor(
				placeRepository, 
				folderPlaceRepository, 
				null, 
				userId
			);

		// 2. Context 구성
		PaginationContext<Place, Long> context = PaginationContext.<Place, Long>builder()
			.request(pageRequest)
			.queryExecutor(executor)
			.idExtractor(Place::getId)
			.build();

		// 3. 페이징 실행
		return paginationService.paginate(context);
	}

	public List<Place> getFocusPlaces(double latitude, double longitude, double radius) {
		return placeRepository.findFocusPlaces(
			latitude,
			longitude,
			radius // km 단위
		);
	}
}