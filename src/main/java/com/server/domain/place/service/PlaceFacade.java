package com.server.domain.place.service;

import com.server.domain.album.entity.Album;
import com.server.domain.album.service.AlbumService;
import com.server.domain.category.dto.CategoryDto;
import com.server.domain.category.entity.Category;
import com.server.domain.category.service.CategoryService;
import com.server.domain.folder.entity.Folder;
import com.server.domain.folder.entity.FolderPlace;
import com.server.domain.folder.service.FolderService;
import com.server.domain.photo.dto.PhotoDto;
import com.server.domain.photo.entity.Photo;
import com.server.domain.photo.service.PhotoService;
import com.server.domain.place.dto.CreatePlaceDto;
import com.server.domain.place.dto.CreatePlaceResponse;
import com.server.domain.place.dto.GetPlaceDetailResponse;
import com.server.domain.place.dto.LocationDto;
import com.server.domain.place.dto.PlaceStatus;
import com.server.domain.place.dto.RegisterPhotoRequest;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceFacade {

	private final static int PLACE_DEFAULT_PHOTO_LIMIT = 30;
	private final static int PLACE_DEFAULT_REVIEW_LIMIT = 4;
	private final PlaceService placeService;
	private final AlbumService albumService;
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
		Album album = albumService.setDefaultAlbum(savedPlace, user);
		photoService.uploadPhotos(album, user, createPlaceDto.getImageUrls());

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

		Album album = albumService.getAlbumByPlaceId(place.getId());
		int totalPhotoCount = photoService.getTotalPhotoCountByAlbum(album);
		List<PhotoDto> photos = photoService.getTopPhotosByAlbum(
				album,
				PLACE_DEFAULT_PHOTO_LIMIT)
			.stream()
			.map(PhotoDto::from)
			.toList();

		boolean hasMorePhotos = totalPhotoCount > PLACE_DEFAULT_PHOTO_LIMIT;

		// 리뷰는 최대 4개까지만 보여줌
		List<Review> reviews = reviewService.getTopReviewsByPlace(place,
			PLACE_DEFAULT_REVIEW_LIMIT);

		// 리뷰에 달린 사진들을 한 번의 쿼리로 모두 가져오기
		Map<Long, List<String>> reviewToPhotoUrls = photoService.getPhotosGroupedByReview(reviews,
			album);

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
			.hasMorePhotos(hasMorePhotos)
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

	public String registerPhotos(User user, Long placeId, RegisterPhotoRequest request) {
		Place place = placeService.getPlaceById(placeId);
		Album album = albumService.getAlbumByPlaceId(place.getId());
		photoService.uploadPhotos(album, user, request.getImageUrls());
		return "Photos uploaded successfully.";
	}

	public CursorPageDto<PhotoDto, Long> getPlacePhotos(Long placeId,
		ApiCursorPaginationRequest pageRequest) {
		Place place = placeService.getPlaceById(placeId);
		Album album = albumService.getAlbumByPlaceId(place.getId());
		CursorPageDto<Photo, Long> page = photoService.getPhotosByAlbumWithCursor(album,
			pageRequest);

		// Photo -> PhotoDto 변환
		return page.map(PhotoDto::from);
	}

	@Transactional(readOnly = true)
	public PhotoDto getPlacePhoto(Long placeId, Long photoId) {
		Photo photo = photoService.getPhotoById(photoId);
		Album album = photo.getAlbum();

		if (album == null) {
			throw new BusinessException(AlbumErrorCode.ALBUM_NOT_FOUND);
		}

		Long albumId = album.getId();
		Place place = albumService.getPlaceByAlbumId(albumId);

		if (!place.getId().equals(placeId)) {
			throw new BusinessException(AlbumErrorCode.PLACE_NOT_MATCHED);
		}

		return PhotoDto.from(photo);
	}
}
