package com.server.domain.photo.executor;

import com.server.domain.photo.entity.Photo;
import com.server.domain.photo.entity.PhotoType;
import com.server.domain.photo.repository.PhotoRepository;
import com.server.global.pagination.executor.CursorQueryExecutor;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Photo 엔티티를 위한 커서 쿼리 실행자
 * 
 * <p>ID 기준으로 정렬하여 최근 생성된 사진부터 조회합니다.
 */
@RequiredArgsConstructor
public class PhotoCursorQueryExecutor implements CursorQueryExecutor<Photo, Long> {

	private final PhotoRepository photoRepository;
	private final Long placeId;
	private final PhotoType photoType;

	@Override
	public List<Photo> findNext(Long cursor, int limit) {
		Pageable pageable = PageRequest.of(0, limit);
		
		if (cursor == null) {
			return photoRepository.findPhotosByPlaceIdAndType(placeId, photoType, pageable);
		}
		return photoRepository.findNextPhotosByPlaceIdAndType(placeId, photoType, cursor, pageable);
	}

	@Override
	public List<Photo> findPrev(Long cursor, int limit) {
		if (cursor == null) {
			return List.of();
		}
		
		Pageable pageable = PageRequest.of(0, limit);
		return photoRepository.findPrevPhotosByPlaceIdAndType(placeId, photoType, cursor, pageable);
	}

	@Override
	public boolean existsNext(Long cursor) {
		if (cursor == null) {
			return false;
		}
		return photoRepository.existsNextPhotoByPlaceIdAndType(placeId, photoType, cursor);
	}

	@Override
	public boolean existsPrev(Long cursor) {
		if (cursor == null) {
			return false;
		}
		return photoRepository.existsPrevPhotoByPlaceIdAndType(placeId, photoType, cursor);
	}

	@Override
	public long countTotal() {
		return photoRepository.countPhotosByPlaceIdAndType(placeId, photoType);
	}
}
