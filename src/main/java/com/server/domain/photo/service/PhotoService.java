package com.server.domain.photo.service;

import com.server.domain.album.entity.Album;
import com.server.domain.photo.entity.Photo;
import com.server.domain.photo.repository.PhotoRepository;
import com.server.domain.user.entity.User;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PhotoService {

	private final PhotoRepository photoRepository;

	public void saveAll(List<Photo> photos) {
		photoRepository.saveAll(photos);
	}

	public List<String> getPhotoUrls(Album album) {

		return photoRepository.findImageUrlsByAlbum(album);
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

	public List<Photo> getPhotosByAlbum(Album album, int limit) {
		return photoRepository.findByAlbum(album, PageRequest.of(0, limit));
	}

	public List<Photo> getPhotosByAlbumAndUser(Album album, User reviewer) {
		return photoRepository.findAllByAlbumAndUser(album, reviewer);
	}

	public Map<Long, List<Photo>> getPhotosByAlbumIds(List<Long> albumIds) {
		List<Photo> photos = photoRepository.findAllByAlbumIds(albumIds);
		return photos.stream()
			.collect(Collectors.groupingBy(photo -> photo.getAlbum().getId()));
	}
}
