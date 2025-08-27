package com.server.domain.folder.service;

import com.server.domain.folder.dto.CreateFolderDto;
import com.server.domain.folder.dto.FolderOptionDto;
import com.server.domain.folder.dto.FolderSummaryDto;
import com.server.domain.folder.dto.UpdateFolderDto;
import com.server.domain.folder.entity.Folder;
import com.server.domain.folder.entity.FolderColor;
import com.server.domain.folder.entity.FolderPlace;
import com.server.domain.folder.entity.FolderType;
import com.server.domain.folder.repository.FolderPlaceRepository;
import com.server.domain.folder.repository.FolderRepository;
import com.server.domain.place.entity.Place;
import com.server.domain.user.entity.User;
import com.server.global.error.code.FolderErrorCode;
import com.server.global.error.exception.BusinessException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FolderService {

	private static final String DEFAULT_FOLDER_NAME = "내 장소";
	private static final FolderColor DEFAULT_FOLDER_COLOR = FolderColor.PINK;
	private final FolderRepository folderRepository;
	private final FolderPlaceRepository folderPlaceRepository;

	@Transactional
	public FolderSummaryDto createFolder(User user, CreateFolderDto createFolderDto) {

		String folderName = createFolderDto.getNormalizedName();

		if (folderRepository.existsByUserAndName(user, folderName)) {
			throw new BusinessException(FolderErrorCode.DUPLICATE_FOLDER_NAME);
		}

		Folder folder = Folder.builder()
			.name(folderName)
			.color(createFolderDto.getColor())
			.type(FolderType.CUSTOM)
			.user(user)
			.build();
		folderRepository.save(folder);
		return FolderSummaryDto.from(folder);
	}

	public void createDefaultFolder(User user) {
		Folder folder = Folder.builder()
			.name(DEFAULT_FOLDER_NAME)
			.color(DEFAULT_FOLDER_COLOR)
			.type(FolderType.DEFAULT)
			.user(user)
			.build();

		folderRepository.save(folder);

	}

	@Transactional
	public String updateFolder(Long folderId, User user,
		UpdateFolderDto updateFolderDto) {
		Folder folder = folderRepository.findByIdAndUserId(folderId, user.getId())
			.orElseThrow(() -> new BusinessException(FolderErrorCode.NOT_FOUND));

		if (folder.getType() != FolderType.CUSTOM) {
			throw new BusinessException(FolderErrorCode.ONLY_CUSTOM_FOLDER_CAN_BE_UPDATED);
		}

		folder.updateName(updateFolderDto.getName());
		folder.updateColor(updateFolderDto.getColor());
		return folder.getName() + " updated.";
	}

	@Transactional
	public String deleteFolder(Long folderId, User user) {
		Folder folder = folderRepository.findByIdAndUserId(folderId, user.getId())
			.orElseThrow(() -> new BusinessException(FolderErrorCode.NOT_FOUND));

		if (folder.getType() != FolderType.CUSTOM) {
			throw new BusinessException(FolderErrorCode.ONLY_CUSTOM_FOLDER_CAN_BE_DELETED);
		}

		folderPlaceRepository.deleteByFolderId(folder.getId());
		folderRepository.delete(folder);

		return folder.getName() + " deleted.";
	}

	@Transactional(readOnly = true)
	public Map<Folder, List<Place>> getFolderPlacesMap(User user) {

		List<Folder> folders = folderRepository.findFoldersByUserId(user.getId());
		List<Long> folderIds = folders.stream().map(Folder::getId).toList();

		Map<Long, List<Place>> folderIdToPlaces = getFolderIdToPlacesMap(folderIds);

		return folders.stream()
			.collect(Collectors.toMap(
				f -> f,
				f -> folderIdToPlaces.getOrDefault(f.getId(), List.of())
			));
	}

	@Transactional(readOnly = true)
	public List<FolderOptionDto> getFoldersForPlaceSelection(Place place, User user) {
		List<Folder> folders = folderRepository.findFoldersByUserId(user.getId());
		List<Long> folderIds = folders.stream().map(Folder::getId).toList();

		Map<Long, List<Place>> folderIdToPlaces = getFolderIdToPlacesMap(folderIds);

		return folders.stream()
			.map(folder -> {
				List<Place> places = folderIdToPlaces.getOrDefault(folder.getId(), List.of());
				long bookmarkCount = places.stream().distinct().count(); // 중복 방지 가능
				boolean isBookmarked = places.stream()
					.anyMatch(p -> p.getId().equals(place.getId())); // id 비교 안전
				return FolderOptionDto.from(folder, bookmarkCount, isBookmarked);
			})
			.toList();
	}

	private Map<Long, List<Place>> getFolderIdToPlacesMap(List<Long> folderIds) {
		List<FolderPlace> folderPlaces = folderPlaceRepository.findFolderPlacesByFolderIds(
			folderIds);

		return folderPlaces.stream()
			.collect(Collectors.groupingBy(
				fp -> fp.getFolder().getId(),
				Collectors.mapping(FolderPlace::getPlace, Collectors.toList())
			));
	}


	public Folder getFolderByIdAndUserId(Long folderId, Long userId) {
		return folderRepository.findByIdAndUserId(folderId, userId)
			.orElseThrow(() -> new BusinessException(FolderErrorCode.NOT_FOUND));
	}

	public boolean existsFolderByPlaceIdAndUserId(Long placeId, Long userId) {
		return folderPlaceRepository.findFolderIdsByPlaceIdAndUserId(placeId, userId).size() > 0;
	}

	public Set<Long> getFolderIdsByPlaceIdAndUserId(Long placeId, Long userId) {
		return new HashSet<>(
			folderPlaceRepository.findFolderIdsByPlaceIdAndUserId(placeId, userId)
		);
	}

	public void savePlaceInFolders(List<FolderPlace> folderPlaces) {
		folderPlaceRepository.saveAll(folderPlaces);
	}

	public void deletePlaceInFolders(Set<Long> folderIds, Long placeId, Long userId) {
		folderPlaceRepository.deleteByFolderIdsAndPlaceIdAndOwner(folderIds, placeId, userId);
	}

	public List<Place> getAllPlacesInFolders(Long userId) {
		return folderPlaceRepository.findDistinctPlacesByUserId(userId);
	}
}
