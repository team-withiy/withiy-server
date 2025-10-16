package com.server.domain.photo.service;

import com.server.domain.photo.dto.PhotoDto;
import com.server.domain.photo.entity.Photo;
import com.server.domain.photo.entity.PhotoType;
import com.server.domain.photo.repository.PhotoRepository;
import com.server.domain.place.entity.Place;
import com.server.domain.review.entity.Review;
import com.server.domain.user.entity.User;
import com.server.global.error.code.PhotoErrorCode;
import com.server.global.error.exception.BusinessException;
import com.server.global.pagination.dto.ApiCursorPaginationRequest;
import com.server.global.pagination.dto.CursorPageDto;
import com.server.global.pagination.utils.CursorPaginationUtils;
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
		List<Photo> photos = photoRepository.findTopPhotosByPlaceIdAndType(place.getId(),
			PhotoType.PUBLIC,
			pageable);

		return photos.stream()
			.map(PhotoDto::from)
			.collect(Collectors.toList());
	}

	public long getTotalPlacePhotoCountByPlace(Place place) {
		return photoRepository.countPhotosByPlaceIdAndType(place.getId(), PhotoType.PUBLIC);
	}

	public CursorPageDto<Photo, Long> getPhotosByPlaceWithCursor(Place place,
		ApiCursorPaginationRequest pageRequest) {
		long total;
		int limit = pageRequest.getLimit();
		Long cursor = pageRequest.getCursor();
		boolean hasNext = false;
		boolean hasPrev = false;
		Pageable pageable = PageRequest.of(0, limit + 1);
		List<Photo> fetched;
		total = photoRepository.countPhotosByPlaceIdAndType(place.getId(), PhotoType.PUBLIC);

		if (cursor == null) {
			// 커서가 없으면 첫 페이지: 최신순 limit+1개 조회
			fetched = photoRepository.findTopPhotosByPlaceIdAndType(place.getId(),
				PhotoType.PUBLIC, pageable);
			boolean hasMore = fetched.size() > limit;
			hasNext = hasMore;
			hasPrev = false;

			return CursorPaginationUtils.paginate(
				total,
				fetched,
				limit,
				false,
				cursor,
				hasPrev,
				hasNext,
				Photo::getId
			);
		}
		if (Boolean.TRUE.equals(pageRequest.getPrev())) {

			fetched = photoRepository.findPrevPhotosByPlaceIdAndType(place.getId(),
				PhotoType.PUBLIC, cursor,
				pageable);
			Collections.reverse(fetched);
			boolean hasMore = fetched.size() > limit;
			hasPrev = hasMore;
			hasNext = photoRepository.existsNextPhotoByPlaceIdAndType(place.getId(),
				PhotoType.PUBLIC, cursor);
		} else {
			fetched = photoRepository.findNextPhotosByPlaceIdAndType(place.getId(),
				PhotoType.PUBLIC,
				cursor,
				pageable);
			boolean hasMore = fetched.size() > limit;
			hasNext = hasMore;
			hasPrev = photoRepository.existsPrevPhotoByPlaceIdAndType(place.getId(),
				PhotoType.PUBLIC, cursor);
		}

		return CursorPaginationUtils.paginate(
			total,
			fetched,
			limit,
			Boolean.TRUE.equals(pageRequest.getPrev()),
			cursor,
			hasPrev,
			hasNext,
			Photo::getId
		);
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
		// TODO: 사진 조회 시 DB 레벨에서 limit 적용 필요 (성능 최적화)
		List<Photo> photos = photoRepository.findByPlaceIdInAndType(placeIds,
			PhotoType.PUBLIC);

		Map<Long, List<String>> placeIdToUrls = photos.stream()
			.collect(Collectors.groupingBy(
				photo -> photo.getPlace().getId(),
				Collectors.mapping(Photo::getImgUrl, Collectors.toList())
			));

		// 각 장소별로 최대 PLACE_DEFAULT_PHOTO_LIMIT 개의 사진 URL만 유지
		placeIdToUrls.replaceAll((placeId, urls) ->
			urls.stream().limit(PLACE_DEFAULT_PHOTO_LIMIT).toList()
		);

		return placeIdToUrls;
	}
}
