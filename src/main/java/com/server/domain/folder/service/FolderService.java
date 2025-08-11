package com.server.domain.folder.service;

import com.server.domain.folder.dto.CreateFolderDto;
import com.server.domain.folder.dto.FolderDto;
import com.server.domain.folder.dto.UpdateFolderDto;
import com.server.domain.folder.entity.Folder;
import com.server.domain.folder.entity.FolderPlace;
import com.server.domain.folder.repository.FolderPlaceRepository;
import com.server.domain.folder.repository.FolderRepository;
import com.server.domain.place.entity.Place;
import com.server.domain.place.entity.PlaceBookmark;
import com.server.domain.place.repository.PlaceBookmarkRepository;
import com.server.domain.place.repository.PlaceRepository;
import com.server.domain.user.entity.User;
import com.server.domain.user.repository.UserRepository;
import com.server.global.error.code.FolderErrorCode;
import com.server.global.error.code.PlaceErrorCode;
import com.server.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FolderService {

	private final PlaceRepository placeRepository;
	private final PlaceBookmarkRepository placeBookmarkRepository;
	private final UserRepository userRepository;
	private final FolderRepository folderRepository;
	private final FolderPlaceRepository folderPlaceRepository;


	@Transactional
	public FolderDto createFolderAndBookmarkPlace(User user, CreateFolderDto createFolderDto,
		Long placeId) {

		String folderName = createFolderDto.getNormalizedName();
		if (folderRepository.existsByUserAndName(user, folderName)) {
			throw new BusinessException(FolderErrorCode.DUPLICATE_FOLDER_NAME);
		}

		Folder folder = Folder.builder()
			.name(folderName)
			.color(createFolderDto.getColor())
			.user(user)
			.build();
		folderRepository.save(folder);

		Place place = placeRepository.findById(placeId)
			.orElseThrow(() -> new BusinessException(PlaceErrorCode.NOT_FOUND));

		if (!placeBookmarkRepository.existsByPlaceIdAndUserId(place.getId(), user.getId())) {
			placeBookmarkRepository.save(
				PlaceBookmark.builder()
					.place(place)
					.user(user)
					.build()
			);
		}

		FolderPlace folderPlace = FolderPlace.builder()
			.folder(folder)
			.place(place)
			.build();

		folderPlaceRepository.save(folderPlace);
		folderRepository.save(folder);
		userRepository.save(user);
		placeRepository.save(place);

		return FolderDto.from(folder);

	}

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
		folderPlaceRepository.deleteByFolder(folder);
		folderRepository.delete(folder);

		return folder.getName() + " deleted.";
	}
}
