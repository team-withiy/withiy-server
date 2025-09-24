package com.server.domain.place.service;

import com.server.domain.album.service.AlbumService;
import com.server.domain.category.dto.CategoryDto;
import com.server.domain.category.entity.Category;
import com.server.domain.category.service.CategoryService;
import com.server.domain.folder.repository.FolderPlaceRepository;
import com.server.domain.photo.service.PhotoService;
import com.server.domain.place.dto.PlaceDetailDto;
import com.server.domain.place.dto.PlaceDto;
import com.server.domain.place.dto.PlaceFocusDto;
import com.server.domain.place.dto.PlaceStatus;
import com.server.domain.place.dto.UpdatePlaceDto;
import com.server.domain.place.entity.Place;
import com.server.domain.place.repository.PlaceBookmarkRepository;
import com.server.domain.place.repository.PlaceRepository;
import com.server.domain.review.service.ReviewService;
import com.server.domain.search.dto.SearchSource;
import com.server.global.error.code.PlaceErrorCode;
import com.server.global.error.exception.BusinessException;
import com.server.global.pagination.dto.ApiCursorPaginationRequest;
import com.server.global.pagination.dto.CursorPageDto;
import com.server.global.pagination.utils.CursorPaginationUtils;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlaceService {

	private final PlaceRepository placeRepository;
	private final PlaceBookmarkRepository placeBookmarkRepository;
	private final FolderPlaceRepository folderPlaceRepository;
	private final ReviewService reviewService;
	private final AlbumService albumService;
	private final PhotoService photoService;
	private final CategoryService categoryService;

	public Place save(Place place) {
		return placeRepository.save(place);
	}

	public Place getPlaceById(Long placeId) {
		return placeRepository.findById(placeId)
			.orElseThrow(() -> new BusinessException(PlaceErrorCode.NOT_FOUND));
	}

	@Transactional
	public List<PlaceFocusDto> getMapFocusPlaces(String swLat, String swLng, String neLat,
		String neLng) {

		List<Place> places = placeRepository.findByLatitudeBetweenAndLongitudeBetween(swLat, neLat,
			swLng, neLng);

		return places.stream().map(place ->
			PlaceFocusDto.builder()
				.id(place.getId())
				.name(place.getName())
				.category(CategoryDto.from(place.getCategory()))
				.build()
		).collect(Collectors.toList());
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
	 * @param source  검색 소스 (MAIN, DATE_SCHEDULE 등)
	 * @param keyword 검색 키워드
	 * @return 검색된 장소 목록
	 */
	@Transactional
	public List<PlaceDto> searchPlacesByKeyword(SearchSource source, String keyword) {

		// 검색 소스가 DATE_SCHEDULE인 경우, DB에 장소 정보가 없으면 카카오 검색 API를 호출하여 장소 정보를 가져옵니다.
		List<Place> places = placeRepository.findByNameContainingIgnoreCase(keyword);
		if (places.isEmpty() && source == SearchSource.DATE_SCHEDULE) {
			// TODO : 카카오 검색 API를 호출하여 장소 정보를 가져오는 로직을 구현해야 합니다.
		}
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

	public long getBookmarkCount(Place place) {
		return placeBookmarkRepository.countByPlaceAndNotDeleted(place);
	}

	public CursorPageDto<Place, Long> getPlacesByFolder(Long folderId,
		ApiCursorPaginationRequest pageRequest) {
		long total;
		int limit = pageRequest.getLimit();
		Long cursor = pageRequest.getCursor();
		boolean hasNext = false;
		boolean hasPrev = false;
		Pageable pageable = PageRequest.of(0, limit + 1);
		List<Place> fetched;

		total = folderPlaceRepository.countPlacesInFolder(folderId);

		if (Boolean.TRUE.equals(pageRequest.getPrev())) {
			List<Long> ids = folderPlaceRepository.findPrevPlaceIdsByFolder(folderId,
				pageRequest.getCursor(), pageable);
			fetched = ids.isEmpty() ? List.of()
				: placeRepository.findPlacesByIds(ids, Sort.by(Sort.Direction.ASC, "id"));
			Collections.reverse(fetched);
			boolean hasMore = fetched.size() > limit;
			hasPrev = hasMore;
			hasNext = folderPlaceRepository.existsNextPlaceByFolder(folderId,
				cursor);
		} else {
			List<Long> ids = folderPlaceRepository.findNextPlaceIdsByFolder(folderId,
				cursor, pageable);
			fetched = ids.isEmpty() ? List.of()
				: placeRepository.findPlacesByIds(ids, Sort.by(Sort.Direction.DESC, "id"));
			boolean hasMore = fetched.size() > limit;
			hasNext = hasMore;
			hasPrev = folderPlaceRepository.existsPrevPlaceByFolder(folderId, cursor);
		}

		return CursorPaginationUtils.paginate(
			total,
			fetched,
			limit,
			Boolean.TRUE.equals(pageRequest.getPrev()),
			cursor,
			hasPrev,
			hasNext,
			Place::getId // 커서 기준 값 추출 방법 전달
		);
	}

	public CursorPageDto<Place, Long> getAllPlacesInFolders(Long userId,
		ApiCursorPaginationRequest pageRequest) {
		long total = folderPlaceRepository.countDistinctPlacesByUser(userId);
		int limit = pageRequest.getLimit();
		Long cursor = pageRequest.getCursor();
		boolean hasNext = false;
		boolean hasPrev = false;
		Pageable pageable = PageRequest.of(0, limit + 1);

		List<Place> fetched;
		if (Boolean.TRUE.equals(pageRequest.getPrev())) {
			List<Long> ids = folderPlaceRepository.findPrevPlaceIdsByUser(userId,
				cursor, pageable);
			fetched = ids.isEmpty() ? List.of()
				: placeRepository.findPlacesByIds(ids, Sort.by(Sort.Direction.ASC, "id"));
			Collections.reverse(fetched);
			boolean hasMore = fetched.size() > limit;
			hasPrev = hasMore;
			hasNext = folderPlaceRepository.existsNextPlaceByUser(userId, cursor);
		} else {
			List<Long> ids = folderPlaceRepository.findNextPlaceIdsByUser(userId,
				cursor, pageable);
			fetched = ids.isEmpty() ? List.of()
				: placeRepository.findPlacesByIds(ids, Sort.by(Sort.Direction.DESC, "id"));
			boolean hasMore = fetched.size() > limit;
			hasNext = hasMore;
			hasPrev = folderPlaceRepository.existsPrevPlaceByUser(userId, cursor);
		}

		return CursorPaginationUtils.paginate(
			total,
			fetched,
			limit,
			Boolean.TRUE.equals(pageRequest.getPrev()),
			cursor,
			hasPrev,
			hasNext,
			Place::getId // 커서 기준 값 추출 방법 전달
		);
	}

	public List<Place> getNearbyPlaces(double latitude, double longitude, double radius) {
		return placeRepository.findNearbyPlaces(
			latitude,
			longitude,
			radius // km 단위
		);
	}
}
