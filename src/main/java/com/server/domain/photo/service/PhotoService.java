package com.server.domain.photo.service;

import com.server.domain.album.entity.Album;
import com.server.domain.photo.dto.PhotoDto;
import com.server.domain.photo.entity.Photo;
import com.server.global.dto.ImageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PhotoService {
    private final PhotoRepository photoRepository;

    public List<PhotoDto> convertToPhotoDtos(List<ImageResponseDto> imageDtos) {

        List<PhotoDto> photoDtos = new ArrayList<>();

        for (ImageResponseDto imgDto : imageDtos) {
            PhotoDto photoDto = PhotoDto.builder()
                .imgUrl(imgDto.getImageUrl())
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

    public void savePhotos(Album album, List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return;
        }
        List<Photo> photos = imageUrls.stream()
            .map(imageUrl -> Photo.builder()
                .imgUrl(imageUrl)
                .album(album)
                .build())
            .toList();
        saveAll(photos);
    }
}
