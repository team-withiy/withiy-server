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
import java.util.List;
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
		Folder folder = folderRepository.findByIdAndUser(folderId, user)
			.orElseThrow(() -> new BusinessException(FolderErrorCode.NOT_FOUND));
		folder.updateName(updateFolderDto.getName());
		folder.updateColor(updateFolderDto.getColor());
		folderRepository.save(folder);
		return FolderDto.from(folder);
	}

	@Transactional
	public String deleteFolder(Long folderId, User user) {
		Folder folder = folderRepository.findByIdAndUser(folderId, user)
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

	public Folder getFolderByIdAndUser(Long folderId, User user) {
		return folderRepository.findByIdAndUser(folderId, user)
			.orElseThrow(() -> new BusinessException(FolderErrorCode.NOT_FOUND));
	}

	public void validatePlaceNotInFolder(Long folderId, Long placeId) {
		if (folderPlaceRepository.existsByFolderIdAndPlaceId(folderId, placeId)) {
			throw new BusinessException(FolderErrorCode.DUPLICATE_PLACE_IN_FOLDER);
		}
	}

	public void validatePlaceInFolder(Long folderId, Long placeId) {
		if (!folderPlaceRepository.existsByFolderIdAndPlaceId(folderId, placeId)) {
			throw new BusinessException(FolderErrorCode.PLACE_NOT_IN_FOLDER);
		}
	}

	public void savePlaceInFolder(FolderPlace folderPlace) {
		folderPlaceRepository.save(folderPlace);
	}

	public void deletePlaceInFolder(FolderPlace from) {
		folderPlaceRepository.delete(from);
	}
}
