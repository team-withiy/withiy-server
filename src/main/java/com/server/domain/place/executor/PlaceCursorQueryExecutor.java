package com.server.domain.place.executor;

import com.server.domain.folder.repository.FolderPlaceRepository;
import com.server.domain.place.entity.Place;
import com.server.domain.place.repository.PlaceRepository;
import com.server.global.pagination.executor.CursorQueryExecutor;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Place 엔티티를 위한 커서 쿼리 실행자
 * 
 * <p>폴더 내의 장소를 ID 기준으로 정렬하여 조회합니다.
 */
@RequiredArgsConstructor
public class PlaceCursorQueryExecutor implements CursorQueryExecutor<Place, Long> {

	private final PlaceRepository placeRepository;
	private final FolderPlaceRepository folderPlaceRepository;
	private final Long folderId;
	private final Long userId; // null이면 특정 폴더, 아니면 전체 폴더

	@Override
	public List<Place> findNext(Long cursor, int limit) {
		Pageable pageable = PageRequest.of(0, limit);
		
		List<Long> placeIds;
		if (userId == null) {
			// 특정 폴더의 장소 조회
			placeIds = folderPlaceRepository.findNextPlaceIdsByFolder(folderId, cursor, pageable);
		} else {
			// 사용자의 전체 폴더 장소 조회
			placeIds = folderPlaceRepository.findNextPlaceIdsByUser(userId, cursor, pageable);
		}
		
		if (placeIds.isEmpty()) {
			return List.of();
		}
		
		return placeRepository.findPlacesByIds(placeIds, Sort.by(Sort.Direction.DESC, "id"));
	}

	@Override
	public List<Place> findPrev(Long cursor, int limit) {
		if (cursor == null) {
			return List.of();
		}
		
		Pageable pageable = PageRequest.of(0, limit);
		
		List<Long> placeIds;
		if (userId == null) {
			// 특정 폴더의 장소 조회
			placeIds = folderPlaceRepository.findPrevPlaceIdsByFolder(folderId, cursor, pageable);
		} else {
			// 사용자의 전체 폴더 장소 조회
			placeIds = folderPlaceRepository.findPrevPlaceIdsByUser(userId, cursor, pageable);
		}
		
		if (placeIds.isEmpty()) {
			return List.of();
		}
		
		// ASC로 조회 (역순 정렬)
		return placeRepository.findPlacesByIds(placeIds, Sort.by(Sort.Direction.ASC, "id"));
	}

	@Override
	public boolean existsNext(Long cursor) {
		if (cursor == null) {
			return false;
		}
		
		if (userId == null) {
			return folderPlaceRepository.existsNextPlaceByFolder(folderId, cursor);
		} else {
			return folderPlaceRepository.existsNextPlaceByUser(userId, cursor);
		}
	}

	@Override
	public boolean existsPrev(Long cursor) {
		if (cursor == null) {
			return false;
		}
		
		if (userId == null) {
			return folderPlaceRepository.existsPrevPlaceByFolder(folderId, cursor);
		} else {
			return folderPlaceRepository.existsPrevPlaceByUser(userId, cursor);
		}
	}

	@Override
	public long countTotal() {
		if (userId == null) {
			return folderPlaceRepository.countPlacesInFolder(folderId);
		} else {
			return folderPlaceRepository.countDistinctPlacesByUser(userId);
		}
	}
}
