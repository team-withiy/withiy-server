package com.server.domain.folder.service;

import com.server.domain.folder.dto.GetFolderPlacesResponse;
import com.server.domain.folder.dto.PlaceSummaryDto;
import com.server.domain.folder.entity.Folder;
import com.server.domain.place.service.PlaceFacade;
import com.server.domain.user.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FolderFacade {

	private final FolderService folderService;
	private final PlaceFacade placeFacade;

	@Transactional(readOnly = true)
	public GetFolderPlacesResponse getFolder(Long folderId, User user) {
		Folder folder = folderService.getFolderByIdAndUser(folderId, user);
		List<PlaceSummaryDto> placeSummaries = placeFacade.getPlaceSummariesByFolderId(folderId);
		return GetFolderPlacesResponse.from(folder, placeSummaries);
	}
}
