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
import com.server.domain.photo.service.PhotoService;
import com.server.domain.place.dto.CreatePlaceDto;
import com.server.domain.place.dto.CreatePlaceResponse;
import com.server.domain.place.dto.GetPlaceDetailResponse;
import com.server.domain.place.dto.LocationDto;
import com.server.domain.place.dto.PlaceStatus;
import com.server.domain.place.entity.Place;
import com.server.domain.review.dto.ReviewDto;
import com.server.domain.review.service.ReviewService;
import com.server.domain.user.entity.User;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceFacade {

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

		Album album = albumService.getAlbumByPlace(place);
		List<PhotoDto> photos = photoService.getPhotosByAlbum(album)
			.stream()
			.map(PhotoDto::from)
			.toList();

		List<ReviewDto> reviews = reviewService.getReviewsByPlace(place)
			.stream()
			.map(review -> {
				User reviewer = review.getUser();
				List<String> reviewerImageUrls = photoService.getPhotosByAlbumAndUser(album,
						reviewer)
					.stream()
					.map(photo -> photo.getImgUrl())
					.toList();
				return ReviewDto.of(review, reviewer, reviewerImageUrls, place.getName());
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
			.reviews(reviews)
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
}
