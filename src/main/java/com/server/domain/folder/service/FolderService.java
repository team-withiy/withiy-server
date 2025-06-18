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

import java.util.List;

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
    public FolderDto createFolderAndBookmarkPlace(User user, CreateFolderDto createFolderDto, Long placeId) {
        if(folderRepository.findByNameAndUserId(createFolderDto.getName(), user.getId())!=null)
            throw new BusinessException(FolderErrorCode.DUPLICATE_FOLDER_NAME);


        Folder folder = Folder.builder()
                .name(createFolderDto.getName())
                .color(createFolderDto.getColor())
                .user(user)
                .build();
        folderRepository.save(folder);

        Place place = placeRepository.findById(placeId)
                .orElseThrow(()-> new BusinessException(PlaceErrorCode.NOT_FOUND));


        if(!placeBookmarkRepository.existsByPlaceIdAndUserId(place.getId(), user.getId())){
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
        if(folderRepository.findByNameAndUserId(createFolderDto.getName(), user.getId())!=null)
            throw new BusinessException(FolderErrorCode.DUPLICATE_FOLDER_NAME);


        Folder folder = Folder.builder()
                .name(createFolderDto.getName())
                .color(createFolderDto.getColor())
                .user(user)
                .build();

        folderRepository.save(folder);
        userRepository.save(user);

        return FolderDto.from(folder);
    }

    @Transactional
    public FolderDto updateFolder(Long folderId, User user, UpdateFolderDto updateFolderDto) {
        Folder folder = folderRepository.findByIdAndUserId(folderId, user.getId());
        folder.setName(updateFolderDto.getName());
        folder.setColor(updateFolderDto.getColor());
        folderRepository.save(folder);
        return FolderDto.from(folder);
    }

    @Transactional
    public String deleteFolder(Long folderId, User user) {
        Folder folder = folderRepository.findByIdAndUserId(folderId, user.getId());
        log.info("name: "+folder.getName());
        List<FolderPlace> folderPlaces = folder.getFolderPlaces();
        String folderName = folder.getName();
        folderRepository.delete(folder);


        // 삭제된 FolderPlace에 있던 장소들
        for (FolderPlace fp : folderPlaces) {
            Long placeId = fp.getPlace().getId();

            // 이 유저가 이 장소를 다른 폴더에도 저장했는지 확인
            boolean stillSaved = folderPlaceRepository.existsByUserIdAndPlaceId(user.getId(), placeId);
            log.info("정보: "+stillSaved);
            if (!stillSaved) {
                // 다른 폴더에도 저장 안 되어 있으면 bookmark도 삭제
                placeBookmarkRepository.deleteByUserIdAndPlaceId(user.getId(), placeId);
            }
        }
        return folderName + " deleted.";
    }
}
