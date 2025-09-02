package com.server.domain.place.service;

import com.server.domain.album.entity.Album;
import com.server.domain.album.service.AlbumService;
import com.server.domain.category.dto.CategoryDto;
import com.server.domain.category.entity.Category;
import com.server.domain.category.service.CategoryService;
import com.server.domain.folder.repository.FolderPlaceRepository;
import com.server.domain.photo.entity.Photo;
import com.server.domain.photo.service.PhotoService;
import com.server.domain.place.dto.CreatePlaceByUserDto;
import com.server.domain.place.dto.PlaceDetailDto;
import com.server.domain.place.dto.PlaceDto;
import com.server.domain.place.dto.PlaceFocusDto;
import com.server.domain.place.dto.PlaceStatus;
import com.server.domain.place.dto.RegisterPlaceDto;
import com.server.domain.place.dto.UpdatePlaceDto;
import com.server.domain.place.entity.Place;
import com.server.domain.place.entity.PlaceBookmark;
import com.server.domain.place.repository.PlaceBookmarkRepository;
import com.server.domain.place.repository.PlaceRepository;
import com.server.domain.review.entity.Review;
import com.server.domain.review.service.ReviewService;
import com.server.domain.search.dto.BookmarkedPlaceDto;
import com.server.domain.search.dto.SearchSource;
import com.server.domain.user.entity.User;
import com.server.global.dto.pagination.ApiCursorPaginationRequest;
import com.server.global.dto.pagination.CursorPageDto;
import com.server.global.error.code.PlaceErrorCode;
import com.server.global.error.exception.BusinessException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
	public PlaceDto createPlaceFirst(User user, CreatePlaceByUserDto createPlaceByUserDto) {

		// 1. 카테고리 조회
		Category category = categoryService.getCategoryByName(createPlaceByUserDto.getCategory());

		// 2. 장소 생성
		Place place = Place.builder()
			.name(createPlaceByUserDto.getPlaceName())
			.region1depth(createPlaceByUserDto.getRegion1depth())
			.region2depth(createPlaceByUserDto.getRegion2depth())
			.region3depth(createPlaceByUserDto.getRegion3depth())
			.address(createPlaceByUserDto.getAddress())
			.latitude(createPlaceByUserDto.getLatitude())
			.longitude(createPlaceByUserDto.getLongitude())
			.score(0L) // 초기값
			.user(user) // 로그인 유저 등 적절한 User 객체
			.category(category)
			.status(PlaceStatus.ACTIVE) // 기본 상태
			.build();

		placeRepository.save(place);

		// 3. 리뷰 저장
		Review savedReview = reviewService.save(place, user, createPlaceByUserDto.getReview(),
			createPlaceByUserDto.getScore());

		// 4. 앨범 생성 및 저장
		Album album = Album.builder()
			.title(createPlaceByUserDto.getPlaceName())
			.user(user)
			.build();
		Album savedAlbum = albumService.save(place, album); // 내부적으로 PlaceAlbum 생성 포함 가정

		// 5. 사진 생성 및 저장
		List<Photo> photos = createPlaceByUserDto.getPhotos().stream()
			.map(photoDto -> Photo.builder()
				.imgUrl(photoDto.getImageUrl())
				.user(user)
				.album(savedAlbum)
				.build())
			.toList();

		photoService.saveAll(photos);

		return PlaceDto.from(place);
	}

	@Transactional
	public PlaceDto registerPlace(User user, RegisterPlaceDto registerPlaceDto) {
		Place place = placeRepository.findById(registerPlaceDto.getPlaceId())
			.orElseThrow(() -> new BusinessException(PlaceErrorCode.NOT_FOUND));
		Review savedreview = reviewService.save(place, user, registerPlaceDto.getReview(),
			registerPlaceDto.getScore());
		Album album = albumService.getAlbumByPlace(place);

		List<Photo> photos = registerPlaceDto.getPhotos().stream()
			.map(photoDto -> Photo.builder()
				.imgUrl(photoDto.getImageUrl())
				.album(album)
				.build())
			.toList();

		photoService.saveAll(photos);
		placeRepository.save(place);

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
	 * 사용자가 북마크한 장소 목록을 조회합니다.
	 *
	 * @param user 인증된 사용자 정보
	 * @return 사용자가 북마크한 장소 목록
	 */
	@Transactional
	public List<BookmarkedPlaceDto> getBookmarkedPlaces(User user) {

		List<PlaceBookmark> placeBookmarks = placeBookmarkRepository.findByUserWithPlace(user);

		return placeBookmarks.stream()
			.map(PlaceBookmark::getPlace)
			.map(BookmarkedPlaceDto::from)
			.collect(Collectors.toList());
	}

	/**
	 * 키워드로 장소를 검색합니다.
	 *
	 * @param source  검색 소스 (MAIN, DATE_SCHEDULE 등)
	 * @param keyword 검색 키워드
	 * @param user    현재 사용자
	 * @return 검색된 장소 목록
	 */
	@Transactional
	public List<PlaceDto> searchPlacesByKeyword(SearchSource source, String keyword, User user) {

		// 검색 소스가 DATE_SCHEDULE인 경우, DB에 장소 정보가 없으면 네이버 검색 API를 호출하여 장소 정보를 가져옵니다.
		List<Place> places = placeRepository.findByNameContainingIgnoreCase(keyword);
		if (places.isEmpty() && source == SearchSource.DATE_SCHEDULE) {
			// TODO : 네이버 검색 API를 호출하여 장소 정보를 가져오는 로직을 구현해야 합니다.
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

	public CursorPageDto<Place> getPlacesByFolder(Long folderId,
		ApiCursorPaginationRequest pageRequest) {
		int limit = pageRequest.getLimit();
		Pageable pageable = PageRequest.of(0, limit + 1);
		List<Place> fetched;

		// 1. Prev 모드인지 Next 모드인지 분기
		if (Boolean.TRUE.equals(pageRequest.getPrev())) {
			// prev 모드: cursor보다 큰 값 ASC로 가져온 뒤 뒤집기
			fetched = folderPlaceRepository.findPrevPlaces(folderId, pageRequest.getCursor(),
				pageable);
			Collections.reverse(fetched);
		} else {
			// next 모드: cursor보다 작은 값 DESC로 가져오기
			fetched = folderPlaceRepository.findNextPlaces(folderId, pageRequest.getCursor(),
				pageable);
		}

		// 2. hasNext / hasPrev 판별 (limit+1 조회 결과 기준)
		boolean hasMore = fetched.size() > limit;

		boolean hasNext;
		boolean hasPrev;

		if (Boolean.TRUE.equals(pageRequest.getPrev())) {
			hasPrev = hasMore;                      // limit+1 → 이전 페이지가 더 있음
			hasNext = pageRequest.getCursor() != null; // cursor가 있으면 이후 페이지가 있음
		} else {
			hasNext = hasMore;                      // limit+1 → 다음 페이지가 더 있음
			hasPrev = pageRequest.getCursor() != null; // cursor가 있으면 이전 페이지가 있음
		}

		// 3. 실제 반환할 데이터만 잘라내기
		List<Place> places = hasMore ? fetched.subList(0, limit) : fetched;

		// 4. 커서 설정
		Long nextCursor = null;
		Long prevCursor = null;

		if (!places.isEmpty()) {
			// hasNext/hasPrev 여부에 따라 커서 값 조정
			nextCursor = hasNext ? places.get(places.size() - 1).getId() : null;
			prevCursor = hasPrev ? places.get(0).getId() : null;
		}

		return CursorPageDto.<Place>builder()
			.data(places)
			.hasNext(hasNext)
			.hasPrev(hasPrev)
			.total(places.size()) // 현재 페이지 개수, 필요하다면 COUNT(*)로 전체 건수 넣을 수 있음
			.nextCursor(nextCursor)
			.prevCursor(prevCursor)
			.build();
	}
}
