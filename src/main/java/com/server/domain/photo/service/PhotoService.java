package com.server.domain.photo.service;

import com.server.domain.album.entity.Album;
import com.server.domain.photo.dto.PhotoDto;
import com.server.domain.photo.entity.Photo;
import com.server.domain.photo.repository.PhotoRepository;
import com.server.domain.user.entity.User;
import com.server.global.dto.ImageResponseDto;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PhotoService {

	private final PhotoRepository photoRepository;

	public List<PhotoDto> convertToPhotoDtos(List<ImageResponseDto> imageDtos) {

		List<PhotoDto> photoDtos = new ArrayList<>();

		for (ImageResponseDto imgDto : imageDtos) {
			PhotoDto photoDto = PhotoDto.builder()
				.imageUrl(imgDto.getImageUrl())
				.build();

			photoDtos.add(photoDto);
		}

		return photoDtos;
	}

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

	public List<Photo> getPhotosByAlbum(Album album) {
		return photoRepository.findAllByAlbum(album);
	}

	public List<Photo> getPhotosByAlbumAndUser(Album album, User reviewer) {
		return photoRepository.findAllByAlbumAndUser(album, reviewer);
	}
}
