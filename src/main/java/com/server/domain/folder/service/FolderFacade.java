package com.server.domain.folder.service;

import com.server.domain.album.entity.Album;
import com.server.domain.album.service.AlbumService;
import com.server.domain.folder.dto.GetFolderPlacesResponse;
import com.server.domain.folder.dto.PlaceSummaryDto;
import com.server.domain.folder.entity.Folder;
import com.server.domain.folder.entity.FolderPlace;
import com.server.domain.photo.entity.Photo;
import com.server.domain.photo.service.PhotoService;
import com.server.domain.place.entity.Place;
import com.server.domain.place.service.PlaceService;
import com.server.domain.user.entity.User;
import java.util.List;
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
	public GetFolderPlacesResponse getFolder(Long folderId, User user) {
		Folder folder = folderService.getFolderByIdAndUser(folderId, user);
		List<Place> places = placeService.getPlacesByFolderId(folder.getId());
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

	@Transactional
	public String savePlaceInFolder(Long folderId, Long placeId, User user) {
		Folder folder = folderService.getFolderByIdAndUser(folderId, user);
		folderService.validatePlaceNotInFolder(folderId, placeId);
		Place place = placeService.getPlaceById(placeId);
		folderService.savePlaceInFolder(FolderPlace.from(folder, place));
		return "Place saved in folder successfully.";
	}
}
