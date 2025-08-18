package com.server.domain.folder.service;

import com.server.domain.folder.dto.CreateFolderDto;
import com.server.domain.folder.dto.FolderDto;
import com.server.domain.folder.dto.UpdateFolderDto;
import com.server.domain.folder.entity.Folder;
import com.server.domain.folder.entity.FolderPlace;
import com.server.domain.folder.repository.FolderPlaceRepository;
import com.server.domain.folder.repository.FolderRepository;
import com.server.domain.user.entity.User;
import com.server.global.error.code.FolderErrorCode;
import com.server.global.error.exception.BusinessException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FolderService {

	private final FolderRepository folderRepository;
	private final FolderPlaceRepository folderPlaceRepository;

	@Transactional
	public FolderDto createFolder(User user, CreateFolderDto createFolderDto) {

		String folderName = createFolderDto.getNormalizedName();
		if (folderRepository.existsByUserAndName(user, folderName)) {
			throw new BusinessException(FolderErrorCode.DUPLICATE_FOLDER_NAME);
		}

		Folder folder = Folder.builder()
			.name(folderName)
			.color(createFolderDto.getColor())
			.user(user)
			.build();

		return FolderDto.from(folderRepository.save(folder));
	}

	@Transactional
	public FolderDto updateFolder(Long folderId, User user, UpdateFolderDto updateFolderDto) {
		Folder folder = folderRepository.findByIdAndUserId(folderId, user.getId())
			.orElseThrow(() -> new BusinessException(FolderErrorCode.NOT_FOUND));
		folder.updateName(updateFolderDto.getName());
		folder.updateColor(updateFolderDto.getColor());
		folderRepository.save(folder);
		return FolderDto.from(folder);
	}

	@Transactional
	public String deleteFolder(Long folderId, User user) {
		Folder folder = folderRepository.findByIdAndUserId(folderId, user.getId())
			.orElseThrow(() -> new BusinessException(FolderErrorCode.NOT_FOUND));
		folderPlaceRepository.deleteByFolderId(folder.getId());
		folderRepository.delete(folder);

		return folder.getName() + " deleted.";
	}

	@Transactional(readOnly = true)
	public List<FolderDto> getFolders(User user) {
		return folderRepository.findAllByUser(user)
			.stream()
			.map(FolderDto::from)
			.toList();
	}

	public Folder getFolderByFolderIdAndUserId(Long folderId, Long userId) {
		return folderRepository.findByIdAndUserId(folderId, userId)
			.orElseThrow(() -> new BusinessException(FolderErrorCode.NOT_FOUND));
	}

	public Folder getFolderByIdAndUserId(Long folderId, Long userId) {
		return folderRepository.findByIdAndUserId(folderId, userId)
			.orElseThrow(() -> new BusinessException(FolderErrorCode.NOT_FOUND));
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
}
