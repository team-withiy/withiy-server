package com.server.domain.photo.service;

import com.server.domain.photo.dto.PhotoDto;
import com.server.domain.photo.dto.PhotoSummary;
import com.server.domain.photo.entity.Photo;
import com.server.domain.photo.entity.PhotoType;
import com.server.domain.photo.executor.PhotoCursorQueryExecutor;
import com.server.domain.photo.repository.PhotoRepository;
import com.server.domain.place.entity.Place;
import com.server.domain.review.entity.Review;
import com.server.domain.user.entity.User;
import com.server.global.error.code.PhotoErrorCode;
import com.server.global.error.exception.BusinessException;
import com.server.global.pagination.dto.ApiCursorPaginationRequest;
import com.server.global.pagination.dto.CursorPageDto;
import com.server.global.pagination.executor.CursorQueryExecutor;
import com.server.global.pagination.service.PaginationService;
import com.server.global.pagination.strategy.PaginationContext;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PhotoService {

	private final PhotoRepository photoRepository;
	private final PaginationService paginationService;
	private final static int REVIEW_DEFAULT_PHOTO_LIMIT = 4;
	private final static int PLACE_DEFAULT_PHOTO_LIMIT = 30;

	public void save(Photo photo) {
		photoRepository.save(photo);
	}

	public void saveAll(List<Photo> photos) {
		photoRepository.saveAll(photos);
	}

	public void uploadPhotos(User uploader, Place place, List<String> imageUrls, PhotoType type) {
		if (imageUrls == null || imageUrls.isEmpty()) {
			return;
		}

		List<Photo> photos = imageUrls.stream()
			.map(imageUrl -> Photo.of(imageUrl, place, type, uploader))
			.toList();
		saveAll(photos);
	}

	public List<PhotoDto> getPlaceTopPhotos(Place place) {
		Pageable pageable = PageRequest.of(0, PLACE_DEFAULT_PHOTO_LIMIT);
		List<Photo> photos = photoRepository.findPhotosByPlaceIdAndType(place.getId(),
			PhotoType.PUBLIC,
			pageable);

		return photos.stream()
			.map(PhotoDto::from)
			.collect(Collectors.toList());
	}

	public long getTotalPlacePhotoCountByPlace(Place place) {
		return photoRepository.countPhotosByPlaceIdAndType(place.getId(), PhotoType.PUBLIC);
	}

	/**
	 * 커서 기반 페이징으로 장소의 사진 조회
	 * 
	 * <p>ID 기준으로 정렬하여 최근 생성된 사진부터 조회합니다.
	 *
	 * @param place       장소
	 * @param pageRequest 페이징 요청
	 * @return 페이징된 사진 목록
	 */
	@Transactional(readOnly = true)
	public CursorPageDto<Photo, Long> getPhotosByPlaceWithCursor(
		Place place,
		ApiCursorPaginationRequest pageRequest) {

		// 1. Executor 생성
		CursorQueryExecutor<Photo, Long> executor = 
			new PhotoCursorQueryExecutor(photoRepository, place.getId(), PhotoType.PUBLIC);

		// 2. Context 구성
		PaginationContext<Photo, Long> context = PaginationContext.<Photo, Long>builder()
			.request(pageRequest)
			.queryExecutor(executor)
			.idExtractor(Photo::getId)
			.build();

		// 3. 페이징 실행
		return paginationService.paginate(context);
	}

	public Photo getPhotoById(Long photoId) {
		return photoRepository.findById(photoId)
			.orElseThrow(() -> new BusinessException(PhotoErrorCode.PHOTO_NOT_FOUND));
	}

	/**
	 * 리뷰 목록과 장소를 받아, 각 리뷰 작성자가 해당 앨범에 올린 사진 URL을 리뷰 ID 기준으로 매핑하여 반환
	 *
	 * @param reviews 리뷰 목록
	 * @param place   장소
	 * @return 리뷰 ID를 키로, 해당 리뷰 작성자가 앨범에 올린 사진 URL 목록을 값으로 하는 맵
	 */
	@Transactional(readOnly = true)
	public Map<Long, List<String>> getPhotosGroupedByReview(List<Review> reviews, Place place) {
		Pageable pageable = PageRequest.of(0, REVIEW_DEFAULT_PHOTO_LIMIT);

		return reviews.stream()
			.collect(Collectors.toMap(
				review -> review.getId(),
				review -> photoRepository.findImageUrlsByPlaceIdAndType(place.getId(),
					PhotoType.PUBLIC, pageable)
			));
	}

	public List<String> getLimitedPhotoUrlsByPlaceIds(List<Long> placeIds, int limit) {
		return photoRepository.findImageUrlsByPlaceIdsAndType(placeIds, PhotoType.PUBLIC,
			PageRequest.of(0, limit));
	}

	public List<String> getLimitedPhotoUrlsByPlaceId(Long placeId, int limit) {
		return photoRepository.findImageUrlsByPlaceIdAndType(placeId, PhotoType.PUBLIC,
			PageRequest.of(0, limit));
	}

	public Map<Long, List<String>> getPlacePhotoUrlsMap(List<Long> placeIds) {
		// DB 레벨에서 ROW_NUMBER() 윈도우 함수를 사용하여 각 장소별로 제한된 사진만 조회
		List<Photo> photos = photoRepository.findLimitedPhotosPerPlace(
			placeIds,
			PhotoType.PUBLIC.name(),
			PLACE_DEFAULT_PHOTO_LIMIT
		);

		return photos.stream()
			.collect(Collectors.groupingBy(
				photo -> photo.getPlace().getId(),
				Collectors.mapping(Photo::getImgUrl, Collectors.toList())
			));
	}

	public Map<Long, List<PhotoSummary>> getPlacePhotoSummariesMap(List<Long> placeIds) {
		// DB 레벨에서 ROW_NUMBER() 윈도우 함수를 사용하여 각 장소별로 제한된 사진만 조회
		List<Photo> photos = photoRepository.findLimitedPhotosPerPlace(
			placeIds,
			PhotoType.PUBLIC.name(),
			PLACE_DEFAULT_PHOTO_LIMIT
		);

		return photos.stream()
			.collect(Collectors.groupingBy(
				photo -> photo.getPlace().getId(),
				Collectors.mapping(PhotoSummary::from, Collectors.toList())
			));
	}

	/**
	 * 장소 별 대표 사진(최신 공개 사진)을 조회하여 Map으로 반환
	 *
	 * @param placeIds 장소 ID 목록
	 * @return 장소 ID를 키로, PhotoDto 리스트를 값으로 하는 맵
	 */
	@Transactional(readOnly = true)
	public Map<Long, List<PhotoDto>> getRepresentativePhotosMap(List<Long> placeIds) {
		if (placeIds == null || placeIds.isEmpty()) {
			return Collections.emptyMap();
		}

		List<Photo> photos = photoRepository.findRepresentativePhotosByPlaceIds(placeIds,
			PhotoType.PUBLIC);

		return photos.stream()
			.collect(Collectors.groupingBy(
				photo -> photo.getPlace().getId(),
				Collectors.mapping(PhotoDto::from, Collectors.toList())
			));
	}

	/**
	 * 루트 별 대표 사진 조회 - 장소가 1~3개인 경우: 첫 번째 장소의 대표 사진 1장 - 장소가 4개 이상인 경우: 처음 4개 장소의 대표 사진 각 1장
	 *
	 * @param placeIds 루트에 속한 장소 ID 목록 (순서대로)
	 * @return 대표 사진 목록
	 */
	@Transactional(readOnly = true)
	public List<PhotoDto> getRouteRepresentativePhotos(List<Long> placeIds) {
		if (placeIds == null || placeIds.isEmpty()) {
			return Collections.emptyList();
		}

		// 1~3개 장소: 첫 번째 장소의 대표 사진 1장
		if (placeIds.size() <= 3) {
			Long firstPlaceId = placeIds.get(0);
			List<Photo> photos = photoRepository.findRepresentativePhotosByPlaceIds(
				List.of(firstPlaceId), PhotoType.PUBLIC);
			return photos.stream()
				.map(PhotoDto::from)
				.collect(Collectors.toList());
		}

		// 4개 이상 장소: 처음 4개 장소의 대표 사진 각 1장
		List<Long> firstFourPlaceIds = placeIds.subList(0, Math.min(4, placeIds.size()));
		List<Photo> photos = photoRepository.findRepresentativePhotosByPlaceIds(
			firstFourPlaceIds, PhotoType.PUBLIC);

		// 장소 순서대로 정렬
		Map<Long, Photo> photoMap = photos.stream()
			.collect(Collectors.toMap(
				photo -> photo.getPlace().getId(),
				photo -> photo,
				(existing, replacement) -> existing
			));

		return firstFourPlaceIds.stream()
			.map(photoMap::get)
			.filter(photo -> photo != null)
			.map(PhotoDto::from)
			.collect(Collectors.toList());
	}
}
