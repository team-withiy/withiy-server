package com.server.domain.place.service;

import com.server.domain.category.dto.CategoryDto;
import com.server.domain.category.entity.Category;
import com.server.domain.category.service.CategoryService;
import com.server.domain.folder.entity.Folder;
import com.server.domain.folder.entity.FolderPlace;
import com.server.domain.folder.service.FolderService;
import com.server.domain.photo.dto.PhotoDto;
import com.server.domain.photo.entity.Photo;
import com.server.domain.photo.entity.PhotoType;
import com.server.domain.photo.service.PhotoService;
import com.server.domain.place.dto.CreatePlaceDto;
import com.server.domain.place.dto.CreatePlaceResponse;
import com.server.domain.place.dto.GetPlaceDetailResponse;
import com.server.domain.place.dto.LocationDto;
import com.server.domain.place.dto.PlaceDto;
import com.server.domain.place.dto.PlaceStatus;
import com.server.domain.place.dto.RegisterPhotoRequest;
import com.server.domain.place.dto.reqeust.NearbyPlaceRequest;
import com.server.domain.place.dto.response.NearbyPlaceResponse;
import com.server.domain.place.entity.Place;
import com.server.domain.review.dto.ReviewDto;
import com.server.domain.review.entity.Review;
import com.server.domain.review.service.ReviewService;
import com.server.domain.user.entity.User;
import com.server.global.error.code.AlbumErrorCode;
import com.server.global.error.exception.BusinessException;
import com.server.global.pagination.dto.ApiCursorPaginationRequest;
import com.server.global.pagination.dto.CursorPageDto;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceFacade {

	private final PlaceService placeService;
	private final CategoryService categoryService;
	private final PhotoService photoService;
	private final ReviewService reviewService;
	private final FolderService folderService;


	@Transactional
	public CreatePlaceResponse registerPlace(User user, CreatePlaceDto createPlaceDto) {
		Category category = categoryService.getCategoryByName(createPlaceDto.getCategoryName());
		Place place = Place.builder()
			.name(createPlaceDto.getName())
			.region1depth(createPlaceDto.getRegion1depth())
			.region2depth(createPlaceDto.getRegion2depth())
			.region3depth(createPlaceDto.getRegion3depth())
			.address(createPlaceDto.getAddress())
			.latitude(createPlaceDto.getLatitude())
			.longitude(createPlaceDto.getLongitude())
			.score(0L)
			.user(user)
			.category(category)
			.status(PlaceStatus.ACTIVE)
			.build();

		Place savedPlace = placeService.save(place);
		photoService.uploadPhotos(user, place, createPlaceDto.getImageUrls(), PhotoType.PUBLIC);

		return CreatePlaceResponse.from(savedPlace);
	}

	@Transactional(readOnly = true)
	public GetPlaceDetailResponse getPlaceDetail(Long placeId) {

		Place place = placeService.getPlaceById(placeId);

		LocationDto location = LocationDto.builder()
			.latitude(place.getLatitude())
			.longitude(place.getLongitude())
			.region1depth(place.getRegion1depth())
			.region2depth(place.getRegion2depth())
			.region3depth(place.getRegion3depth())
			.build();

		long totalPhotoCount = photoService.getTotalPlacePhotoCountByPlace(place);
		List<PhotoDto> photos = photoService.getPlaceTopPhotos(place);

		List<Review> reviews = reviewService.getTopReviewsByPlace(place);

		// 리뷰에 달린 사진들을 한 번의 쿼리로 모두 가져오기
		// TODO: N+1 문제 해결 및 Review 엔티티에 Date 종속 추가하여 Date 기반으로 사진들을 가져오도록 변경
		Map<Long, List<String>> reviewToPhotoUrls = photoService.getPhotosGroupedByReview(reviews,
			place);

		// Review -> ReviewDto 변환
		List<ReviewDto> reviewSummaries = reviews.stream()
			.map(review -> {
				List<String> reviewerImageUrls = reviewToPhotoUrls.getOrDefault(review.getId(),
					List.of());
				return ReviewDto.of(review, review.getUser(), reviewerImageUrls, place.getName());
			})
			.toList();

		return GetPlaceDetailResponse.builder()
			.placeId(place.getId())
			.placeName(place.getName())
			.category(CategoryDto.from(place.getCategory()))
			.address(place.getAddress())
			.location(location)
			.score(place.getScore())
			.photos(photos)
			.totalPhotoCount(totalPhotoCount)
			.reviews(reviewSummaries)
			.build();
	}

	@Transactional
	public String updatePlaceFolders(Set<Long> targetFolderIds, Long placeId, User user) {
		Place place = placeService.getPlaceById(placeId);

		// 현재 사용자의 폴더 목록에서 해당 장소가 속한 폴더 ID를 가져옵니다.
		Set<Long> currentFolderIds = folderService.getFolderIdsByPlaceIdAndUserId(placeId,
			user.getId());

		// 추가할 폴더 ID와 제거할 폴더 ID를 계산합니다.
		Set<Long> toAdd = new HashSet<>(targetFolderIds);
		toAdd.removeAll(currentFolderIds);

		Set<Long> toRemove = new HashSet<>(currentFolderIds);
		toRemove.removeAll(targetFolderIds);

		// 제거할 폴더에서 장소를 삭제합니다.
		folderService.deletePlaceInFolders(toRemove, placeId, user.getId());

		// 추가할 폴더에서 장소를 저장합니다.
		List<FolderPlace> inserts = new ArrayList<>();
		for (Long folderId : toAdd) {
			Folder folder = folderService.getFolderByIdAndUser(folderId, user.getId());
			inserts.add(FolderPlace.from(folder, place));
		}
		folderService.savePlaceInFolders(inserts);

		return "Place updated in folders successfully.";
	}

	public boolean isBookmarked(Long placeId, User user) {
		return folderService.existsFolderByPlaceIdAndUserId(placeId, user.getId());
	}

	@Transactional
	public String registerPhotos(User user, Long placeId, RegisterPhotoRequest request) {
		Place place = placeService.getPlaceById(placeId);
		photoService.uploadPhotos(user, place, request.getImageUrls(), PhotoType.PUBLIC);
		return "사진이 성공적으로 등록되었습니다.";
	}

	public CursorPageDto<PhotoDto, Long> getPlacePhotos(Long placeId,
		ApiCursorPaginationRequest pageRequest) {
		Place place = placeService.getPlaceById(placeId);
		CursorPageDto<Photo, Long> page = photoService.getPhotosByPlaceWithCursor(place,
			pageRequest);

		// Photo -> PhotoDto 변환
		return page.map(PhotoDto::from);
	}

	@Transactional(readOnly = true)
	public PhotoDto getPlacePhoto(Long placeId, Long photoId) {
		Photo photo = photoService.getPhotoById(photoId);

		if (!photo.getPlace().getId().equals(placeId)) {
			throw new BusinessException(AlbumErrorCode.PLACE_NOT_MATCHED);
		}

		return PhotoDto.from(photo);
	}

	@Transactional(readOnly = true)
	public CursorPageDto<ReviewDto, Long> getPlaceReviews(Long placeId,
		ApiCursorPaginationRequest pageRequest) {
		// 장소 조회
		Place place = placeService.getPlaceById(placeId);
		// 장소 리뷰 커서 페이지 조회
		CursorPageDto<Review, Long> page = reviewService.getReviewsByPlaceWithCursor(place,
			pageRequest);
		// Review -> ReviewDto 변환
		List<Review> reviews = page.getData();
		Map<Long, List<String>> reviewToPhotoUrls = photoService.getPhotosGroupedByReview(reviews,
			place);

		return page.map(review -> {
				List<String> reviewerImageUrls = reviewToPhotoUrls.getOrDefault(review.getId(),
					List.of());
				return ReviewDto.of(review, review.getUser(), reviewerImageUrls, place.getName());
			}
		);
	}


	@Transactional(readOnly = true)
	public NearbyPlaceResponse getNearbyPlaces(User user, NearbyPlaceRequest request) {
		List<Place> places = placeService.getNearbyPlaces(
			request.getLatitude(),
			request.getLongitude(),
			request.getRadius() // km 단위
		);

		List<Long> placeIds = places.stream()
			.map(Place::getId)
			.collect(Collectors.toList());

		// 1. 장소별 평균 평점 조회
		Map<Long, Double> placeScoreMap = reviewService.getScoreMapForPlaces(placeIds);
		// 2. 장소별 북마크 여부 조회
		Map<Long, Boolean> placeBookmarkMap = folderService.getBookmarkMapForPlaces(
			placeIds, user.getId());
		// 3. 장소별 사진 URL 조회
		Map<Long, List<String>> placePhotoMap = photoService.getPlacePhotoUrlsMap(placeIds);
		// 4. Place -> PlaceDto 변환
		List<PlaceDto> placeDtos = places.stream()
			.map(place -> PlaceDto.from(
				place,
				placeBookmarkMap.get(place.getId()),
				placeScoreMap.get(place.getId()),
				placePhotoMap.get(place.getId())
			))
			.collect(Collectors.toList());

		return NearbyPlaceResponse.builder()
			.places(placeDtos)
			.build();
	}
}
