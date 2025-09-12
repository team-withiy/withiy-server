package com.server.domain.photo.service;

import com.server.domain.album.entity.Album;
import com.server.domain.folder.repository.FolderPlaceRepository;
import com.server.domain.photo.entity.Photo;
import com.server.domain.photo.repository.PhotoRepository;
import com.server.domain.user.entity.User;
import com.server.global.error.code.PhotoErrorCode;
import com.server.global.error.exception.BusinessException;
import com.server.global.pagination.dto.ApiCursorPaginationRequest;
import com.server.global.pagination.dto.CursorPageDto;
import com.server.global.pagination.utils.CursorPaginationUtils;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PhotoService {

	private final PhotoRepository photoRepository;
	private final FolderPlaceRepository folderPlaceRepository;

	public void saveAll(List<Photo> photos) {
		photoRepository.saveAll(photos);
	}

	// TODO: 페이징 처리 작업 시 삭제 예정
	public List<String> getAllPhotoUrls(Album album) {

		return photoRepository.findAllImageUrlByAlbum(album);
	}

	public List<String> getLimitedPhotoUrls(Album album, int limit) {
		return photoRepository.findImageUrlsByAlbum(album,
			PageRequest.of(0, limit));
	}

	public void uploadPhotos(Album album, User uploader, List<String> imageUrls) {
		if (imageUrls == null || imageUrls.isEmpty()) {
			return;
		}
		List<Photo> photos = imageUrls.stream()
			.map(imageUrl -> Photo.builder()
				.imgUrl(imageUrl)
				.album(album)
				.user(uploader)
				.build())
			.toList();
		saveAll(photos);
	}

	public List<Photo> getLimitedPhotosByAlbum(Album album, int limit) {
		Pageable pageable = PageRequest.of(0, limit);
		return photoRepository.findAllByAlbum(album, pageable);
	}

	public List<Photo> getPhotosByAlbumAndUser(Album album, User reviewer) {
		return photoRepository.findAllByAlbumAndUser(album, reviewer);
	}

	public Map<Long, Photo> getFirstPhotoByAlbumIds(List<Long> albumIds) {
		List<Photo> photos = photoRepository.findAllByAlbumIds(albumIds);
		return photos.stream()
			.collect(Collectors.toMap(photo -> photo.getAlbum().getId(), photo -> photo,
				(existing, replacement) -> existing));
	}

	public int getTotalPhotoCountByAlbum(Album album) {
		return photoRepository.countPhotosByAlbum(album);
	}

	public CursorPageDto<Photo, Long> getPhotosByAlbumWithCursor(Album album,
		ApiCursorPaginationRequest pageRequest) {
		long total;
		int limit = pageRequest.getLimit();
		Long cursor = pageRequest.getCursor();
		boolean hasNext = false;
		boolean hasPrev = false;
		Pageable pageable = PageRequest.of(0, limit + 1);
		List<Photo> fetched;
		total = photoRepository.countPhotosByAlbumId(album.getId());

		if (Boolean.TRUE.equals(pageRequest.getPrev())) {

			fetched = photoRepository.findPrevPhotosByAlbumId(album.getId(), cursor,
				pageable);
			Collections.reverse(fetched);
			boolean hasMore = fetched.size() > limit;
			hasPrev = hasMore;
			hasNext = photoRepository.existsNextPhotoByAlbumId(album.getId(), cursor);
		} else {
			fetched = photoRepository.findNextPhotosByAlbumId(album.getId(), cursor,
				pageable);
			boolean hasMore = fetched.size() > limit;
			hasNext = hasMore;
			hasPrev = photoRepository.existsPrevPhotoByAlbumId(album.getId(), cursor);
		}

		return CursorPaginationUtils.paginate(
			total,
			fetched,
			limit,
			Boolean.TRUE.equals(pageRequest.getPrev()),
			cursor,
			hasPrev,
			hasNext,
			Photo::getId
		);
	}

	public Photo getPhotoById(Long photoId) {
		return photoRepository.findById(photoId)
			.orElseThrow(() -> new BusinessException(PhotoErrorCode.PHOTO_NOT_FOUND));
	}
}
