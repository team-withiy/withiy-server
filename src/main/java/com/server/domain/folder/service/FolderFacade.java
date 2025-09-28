package com.server.domain.folder.service;

import com.server.domain.folder.dto.FolderOptionDto;
import com.server.domain.folder.dto.FolderSummaryDto;
import com.server.domain.folder.dto.PlaceSummaryDto;
import com.server.domain.folder.entity.Folder;
import com.server.domain.photo.service.PhotoService;
import com.server.domain.place.entity.Place;
import com.server.domain.place.service.PlaceService;
import com.server.domain.user.entity.User;
import com.server.global.pagination.dto.ApiCursorPaginationRequest;
import com.server.global.pagination.dto.CursorPageDto;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FolderFacade {

	private static final String ALL_PLACE_FOLDER_NAME = "저장한 모든 장소";
	private static final int DEFAULT_THUMBNAIL_LIMIT = 4;
	private final FolderService folderService;
	private final PlaceService placeService;
	private final PhotoService photoService;

	@Transactional(readOnly = true)
	public CursorPageDto<PlaceSummaryDto, Long> getFolderPlaces(Long folderId, User user,
		ApiCursorPaginationRequest pageRequest) {

		Folder folder = folderService.getFolderByIdAndUser(folderId, user.getId());

		CursorPageDto<Place, Long> page = placeService.getPlacesByFolder(folder.getId(),
			pageRequest);
		return mapToPlaceSummary(page);
	}

	@Transactional(readOnly = true)
	public CursorPageDto<PlaceSummaryDto, Long> getAllFolderPlaces(User user,
		ApiCursorPaginationRequest pageRequest) {

		CursorPageDto<Place, Long> page = placeService.getAllPlacesInFolders(user.getId(),
			pageRequest);
		return mapToPlaceSummary(page);
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

		return FolderSummaryDto.ofVirtual(ALL_PLACE_FOLDER_NAME, totalCount, thumbnails);
	}

	/**
	 * 장소 리스트에서 썸네일 URL 추출 (최대 4개)
	 *
	 * @param places
	 * @return
	 */
	private List<String> extractFirstThumbnails(List<Place> places) {
		List<Long> placeIds = places.stream().map(Place::getId).toList();

		return photoService.getLimitedPhotoUrlsByPlaceIds(placeIds,
			DEFAULT_THUMBNAIL_LIMIT);
	}

	/**
	 * 폴더 내 장소 요약 페이지 매핑 (썸네일 포함)
	 */
	private CursorPageDto<PlaceSummaryDto, Long> mapToPlaceSummary(
		CursorPageDto<Place, Long> page) {

		List<Long> placeIds = page.getData().stream().map(Place::getId).toList();
		Map<Long, List<String>> placePhotoUrlsMap = photoService.getPlacePhotoUrlsMap(placeIds);

		return page.map(place -> {
			List<String> photoUrls = placePhotoUrlsMap.getOrDefault(place.getId(),
				Collections.emptyList());
			return PlaceSummaryDto.from(place, photoUrls);
		});
	}
}
