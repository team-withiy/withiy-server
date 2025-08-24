package com.server.domain.folder.service;

import com.server.domain.album.entity.Album;
import com.server.domain.album.service.AlbumService;
import com.server.domain.folder.dto.FolderOptionDto;
import com.server.domain.folder.dto.FolderSummaryDto;
import com.server.domain.folder.dto.GetFolderPlacesResponse;
import com.server.domain.folder.dto.PlaceSummaryDto;
import com.server.domain.folder.entity.Folder;
import com.server.domain.photo.entity.Photo;
import com.server.domain.photo.service.PhotoService;
import com.server.domain.place.entity.Place;
import com.server.domain.place.service.PlaceService;
import com.server.domain.user.entity.User;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FolderFacade {

	private final FolderService folderService;
	private final PlaceService placeService;
	private final AlbumService albumService;
	private final PhotoService photoService;

	@Transactional(readOnly = true)
	public GetFolderPlacesResponse getFolderPlaces(Long folderId, User user) {
		Folder folder = folderService.getFolderByIdAndUserId(folderId, user.getId());
		List<Place> places = placeService.getPlacesByFolderId(folderId);
		List<PlaceSummaryDto> placeSummaries = places.stream()
			.map(place -> {
				Album album = albumService.getAlbumByPlace(place);
				List<Photo> photos = photoService.getPhotosByAlbum(album);
				List<String> imageUrls = photos
					.stream()
					.map(photo -> photo.getImgUrl())
					.toList();
				return PlaceSummaryDto.from(place, imageUrls);
			})
			.toList();
		return GetFolderPlacesResponse.from(folder, placeSummaries);
	}

	public List<FolderOptionDto> getFoldersForPlaceSelection(Long placeId, User user) {
		Place place = placeService.getPlaceById(placeId);
		return folderService.getFoldersForPlaceSelection(place, user);
	}

	public List<FolderSummaryDto> getFolderSummaries(User user) {
		Map<Folder, List<Place>> folderPlacesMap = folderService.getFolderPlacesMap(user);
		return folderPlacesMap.entrySet().stream()
			.map(entry -> {
				Folder folder = entry.getKey();
				List<Place> places = entry.getValue();
				Long bookmarkCount = (long) places.size();

				List<String> thumbnails = places.stream()
					.map(place -> {
						Album album = albumService.getAlbumByPlace(place);
						List<Photo> photos = photoService.getPhotosByAlbum(album);
						return photos.isEmpty() ? null : photos.get(0).getImgUrl();
					})
					.filter(Objects::nonNull) // null 제거
					.limit(4)
					.toList();

				return FolderSummaryDto.from(folder, bookmarkCount, thumbnails);
			})
			.toList();
	}
}
