package com.server.domain.place.service;

import com.server.domain.album.entity.Album;
import com.server.domain.album.service.AlbumService;
import com.server.domain.category.entity.Category;
import com.server.domain.category.service.CategoryService;
import com.server.domain.photo.service.PhotoService;
import com.server.domain.place.dto.CreatePlaceDto;
import com.server.domain.place.dto.CreatePlaceResponse;
import com.server.domain.place.dto.PlaceStatus;
import com.server.domain.place.entity.Place;
import com.server.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceFacade {
    private final PlaceService placeService;
    private final AlbumService albumService;
    private final CategoryService categoryService;
    private final PhotoService photoService;


    @Transactional
    public CreatePlaceResponse createPlace(User user, CreatePlaceDto createPlaceDto) {
        Category category = categoryService.getCategoryByName(createPlaceDto .getCategoryName());
        Place place = Place.builder()
            .name(createPlaceDto.getName())
            .region1depth(createPlaceDto.getRegion1depth())
            .region2depth(createPlaceDto.getRegion2depth())
            .region3depth(createPlaceDto.getRegion3depth())
            .address(createPlaceDto.getAddress())
            .latitude(createPlaceDto.getLatitude())
            .longitude(createPlaceDto.getLongitude())
            .score(0L)
            .user(user)
            .category(category)
            .status(PlaceStatus.ACTIVE)
            .build();

        Place savedPlace = placeService.save(place);
        Album album = albumService.setDefaultAlbum(savedPlace, user);
        photoService.uploadPhotos(album, user, createPlaceDto.getImageUrls());

        return CreatePlaceResponse.from(savedPlace);
    }
}
