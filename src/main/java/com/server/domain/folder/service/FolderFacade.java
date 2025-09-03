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
import com.server.global.dto.pagination.ApiCursorPaginationRequest;
import com.server.global.dto.pagination.CursorPageDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FolderFacade {

	private static final String ALL_PLACE_FOLDER_NAME = "저장한 모든 장소";
	private final FolderService folderService;
	private final PlaceService placeService;
	private final AlbumService albumService;
	private final PhotoService photoService;

	@Transactional(readOnly = true)
	public CursorPageDto<PlaceSummaryDto, Long> getFolderPlaces(Long folderId, User user,
		ApiCursorPaginationRequest pageRequest) {

		Folder folder = folderService.getFolderByIdAndUser(folderId, user.getId());

		CursorPageDto<Place, Long> page = placeService.getPlacesByFolder(folder.getId(),
			pageRequest);

		List<Long> placeIds = page.getData().stream().map(Place::getId).toList();
		Map<Long, Album> albumMap = albumService.getAlbumsByPlaceIds(placeIds);

		return page.map(place -> {
			Album album = albumMap.get(place.getId());
			List<String> imageUrls = photoService.getLimitedPhotoUrls(album, 4);
			return PlaceSummaryDto.from(place, imageUrls);
		});
	}

	@Transactional(readOnly = true)
	public GetFolderPlacesResponse getAllFolderPlaces(User user) {
		List<Place> places = folderService.getAllPlacesInFolders(user.getId());
		List<Long> placeIds = places.stream().map(Place::getId).toList();

		Map<Long, Album> albumMap = albumService.getAlbumsByPlaceIds(placeIds);
		Map<Long, List<Photo>> photoMap = photoService.getPhotosByAlbumIds(
			albumMap.values().stream().map(Album::getId).toList()
		);

		List<PlaceSummaryDto> placeSummaries = places.stream()
			.map(place -> {
				Album album = albumMap.get(place.getId());
				List<String> imageUrls = photoMap.getOrDefault(album.getId(), List.of())
					.stream()
					.map(Photo::getImgUrl)
					.toList();
				return PlaceSummaryDto.from(place, imageUrls);
			})
			.toList();
		return GetFolderPlacesResponse.ofVirtual(ALL_PLACE_FOLDER_NAME, placeSummaries);
	}

	@Transactional(readOnly = true)
	public List<FolderOptionDto> getFoldersForPlaceSelection(Long placeId, User user) {
		Place place = placeService.getPlaceById(placeId);
		return folderService.getFoldersForPlaceSelection(place, user);
	}

	/**
	 * 폴더 요약 리스트 + 가상 폴더(저장된 모든 장소) 요약 반환
	 *
	 * @param user
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<FolderSummaryDto> getFolderSummaries(User user) {
		Map<Folder, List<Place>> folderPlacesMap = folderService.getFolderPlacesMap(user);

		List<FolderSummaryDto> summaries = getRealFolderSummaries(folderPlacesMap);
		FolderSummaryDto allPlacesFolder = getVirtualFolderSummary(folderPlacesMap);

		List<FolderSummaryDto> result = new ArrayList<>(summaries);
		result.add(0, allPlacesFolder); // 0번 인덱스에 삽입
		return result;
	}

	/**
	 * 실제 폴더 요약 생성
	 *
	 * @param folderPlacesMap
	 * @return
	 */
	private List<FolderSummaryDto> getRealFolderSummaries(
		Map<Folder, List<Place>> folderPlacesMap) {
		return folderPlacesMap.entrySet().stream()
			.map(entry -> {
				Folder folder = entry.getKey();
				List<Place> places = entry.getValue();
				Long bookmarkCount = (long) places.size();

				List<String> thumbnails = extractFirstThumbnails(places);
				return FolderSummaryDto.from(folder, bookmarkCount, thumbnails);
			})
			.toList();
	}

	/**
	 * 가상 폴더(저장된 모든 장소) 요약 생성
	 *
	 * @param folderPlacesMap
	 * @return
	 */
	private FolderSummaryDto getVirtualFolderSummary(Map<Folder, List<Place>> folderPlacesMap) {
		List<Place> allPlaces = folderPlacesMap.values().stream()
			.flatMap(List::stream)
			.distinct()
			.toList();

		Long totalCount = (long) allPlaces.size();
		List<String> thumbnails = extractFirstThumbnails(allPlaces);

		return FolderSummaryDto.ofVirtual("저장된 모든 장소", totalCount, thumbnails);
	}

	/**
	 * 장소 리스트에서 썸네일 URL 추출 (최대 4개)
	 *
	 * @param places
	 * @return
	 */
	private List<String> extractFirstThumbnails(List<Place> places) {
		List<Long> placeIds = places.stream().map(Place::getId).toList();

		// placeId → album
		Map<Long, Album> albumMap = albumService.getAlbumsByPlaceIds(placeIds);

		// albumId → 대표 photo
		Map<Long, Photo> photoMap = photoService.getFirstPhotoByAlbumIds(
			albumMap.values().stream().map(Album::getId).toList()
		);

		return places.stream()
			.map(place -> {
				Album album = albumMap.get(place.getId());
				return (album != null && photoMap.containsKey(album.getId()))
					? photoMap.get(album.getId()).getImgUrl()
					: null;
			})
			.filter(Objects::nonNull)
			.limit(4)
			.toList();
	}
}
