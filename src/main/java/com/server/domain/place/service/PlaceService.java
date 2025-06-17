package com.server.domain.place.service;

import com.server.domain.album.entity.Album;
import com.server.domain.album.repository.AlbumRepository;
import com.server.domain.album.service.AlbumService;
import com.server.domain.category.dto.CategoryDto;
import com.server.domain.category.entity.Category;
import com.server.domain.category.repository.CategoryRepository;
import com.server.domain.photo.entity.Photo;
import com.server.domain.photo.service.PhotoService;
import com.server.domain.place.dto.*;
import com.server.domain.place.entity.Place;
import com.server.domain.place.repository.PlaceBookmarkRepository;
import com.server.domain.place.repository.PlaceRepository;
import com.server.domain.review.entity.Review;
import com.server.domain.review.repository.ReviewRepository;
import com.server.domain.review.service.ReviewService;
import com.server.domain.user.entity.User;
import com.server.domain.user.repository.UserRepository;
import com.server.global.config.S3UrlConfig;
import com.server.global.dto.ImageResponseDto;
import com.server.global.error.code.PlaceErrorCode;
import com.server.global.error.code.UserErrorCode;
import com.server.global.error.exception.BusinessException;
import com.server.global.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlaceService {
        private final PlaceRepository placeRepository;
        private final PlaceBookmarkRepository placeBookmarkRepository;
        private final UserRepository userRepository;
        private final S3UrlConfig s3UrlConfig;
        private final CategoryRepository categoryRepository;
        private final AlbumRepository albumRepository;
        private final ReviewService reviewService;
        private final AlbumService albumService;
        private final PhotoService photoService;

        @Transactional
        public List<PlaceFocusDto> getMapFocusPlaces(String swLat, String swLng, String neLat, String neLng) {

                List<Place> places = placeRepository.findByLatitudeBetweenAndLongitudeBetween(swLat, neLat, swLng, neLng);

                if(places!=null){
                        return places.stream().map(place ->
                                PlaceFocusDto.builder()
                                        .id(place.getId())
                                        .name(place.getName())
                                        .category(CategoryDto.from(place.getCategory()))
                                        .build()
                        ).collect(Collectors.toList());
                }
                else
                        return null;
        }

        @Transactional
        public PlaceDto getPlaceSimpleDetail(Long placeId) {
                Place place = placeRepository.findById(placeId)
                        .orElseThrow(()-> new BusinessException(PlaceErrorCode.NOT_FOUND));

                return PlaceDto.from(place, false);
        }

        @Transactional
        public PlaceDto getPlaceSimpleDetailAfterLogin(Long placeId, Long userId) {
                Place place = placeRepository.findById(placeId)
                        .orElseThrow(()-> new BusinessException(PlaceErrorCode.NOT_FOUND));
                User user = userRepository.findById(userId)
                        .orElseThrow(()-> new BusinessException(UserErrorCode.NOT_FOUND));
                boolean isBookmarked = placeBookmarkRepository.existsByPlaceIdAndUserId(place.getId(), user.getId());
                return PlaceDto.from(place, isBookmarked);
        }

        @Transactional
        public PlaceDetailDto getPlaceDetail(Long placeId) {
                Place place = placeRepository.findById(placeId)
                        .orElseThrow(()-> new BusinessException(PlaceErrorCode.NOT_FOUND));
                return PlaceDetailDto.from(place, false, s3UrlConfig);

        }

        @Transactional
        public PlaceDetailDto getPlaceDetailAfterLogin(Long placeId, Long userId) {
                Place place = placeRepository.findById(placeId)
                        .orElseThrow(()-> new BusinessException(PlaceErrorCode.NOT_FOUND));
                User user = userRepository.findById(userId)
                        .orElseThrow(()-> new BusinessException(UserErrorCode.NOT_FOUND));
                boolean isBookmarked = placeBookmarkRepository.existsByPlaceIdAndUserId(place.getId(), user.getId());
                return PlaceDetailDto.from(place, isBookmarked, s3UrlConfig);
        }

        @Transactional
        public PlaceDto createPlace(CreatePlaceDto createPlaceDto) {
                Category category = categoryRepository.findByName(createPlaceDto.getCategory());
                Place place = new Place(createPlaceDto.getName(), createPlaceDto.getRegion1depth(),
                        createPlaceDto.getRegion2depth(), createPlaceDto.getRegion3depth(), createPlaceDto.getAddress(),
                        createPlaceDto.getLatitude(), createPlaceDto.getLongitude(), category);
                placeRepository.save(place);

                return PlaceDto.from(place, false);
        }

        @Transactional
        public PlaceDto createPlaceFirst(User user, CreatePlaceByUserDto createPlaceByUserDto) {

                Category category = categoryRepository.findByName(createPlaceByUserDto.getCategory());
                Place place = new Place(createPlaceByUserDto.getPlaceName(), createPlaceByUserDto.getAddress(),
                        createPlaceByUserDto.getLatitude(), createPlaceByUserDto.getLongitude(), category, createPlaceByUserDto.getScore());
                Place savedPlace = placeRepository.save(place);

                Review savedreview = reviewService.save(savedPlace, user, createPlaceByUserDto.getReview(),
                                createPlaceByUserDto.getScore());
                savedPlace.addReview(savedreview);

                Album album = new Album(createPlaceByUserDto.getPlaceName(), savedPlace,
                        createPlaceByUserDto.getPlaceName(), user);
                Album savedAlbum = albumService.save(album);
                savedPlace.addAlbum(savedAlbum);

                List<Photo> photos = createPlaceByUserDto.getPhotos().stream()
                        .map(photoDto -> Photo.builder()
                                .imgUrl(photoDto.getImgUrl())
                                .sequence(photoDto.getSequence())
                                .isPrivate(photoDto.isPrivate())
                                .place(savedPlace)
                                .album(savedAlbum)
                                .review(savedreview)
                                .build())
                        .toList();
                photoService.saveAll(photos);

                photos.forEach(place::addPhoto);
                placeRepository.save(place);

                boolean isBookmarked = placeBookmarkRepository.existsByPlaceIdAndUserId(place.getId(), user.getId());
                return PlaceDto.from(savedPlace, isBookmarked);
        }

        @Transactional
        public PlaceDto registerPlace(User user, RegisterPlaceDto registerPlaceDto) {
                Place place = placeRepository.findById(registerPlaceDto.getPlaceId())
                        .orElseThrow(()-> new BusinessException(PlaceErrorCode.NOT_FOUND));
                Review savedreview = reviewService.save(place, user, registerPlaceDto.getReview(),
                        registerPlaceDto.getScore());
                place.addReview(savedreview);

                Album album = new Album(place.getName(), place,
                        place.getName(), user);
                Album savedAlbum = albumService.save(album);
                place.addAlbum(savedAlbum);
                List<Photo> photos = registerPlaceDto.getPhotos().stream()
                        .map(photoDto -> Photo.builder()
                                .imgUrl(photoDto.getImgUrl())
                                .sequence(photoDto.getSequence())
                                .isPrivate(photoDto.isPrivate())
                                .place(place)
                                .album(savedAlbum)
                                .review(savedreview)
                                .build())
                        .toList();

                photoService.saveAll(photos);

                photos.forEach(place::addPhoto);
                placeRepository.save(place);

                boolean isBookmarked = placeBookmarkRepository.existsByPlaceIdAndUserId(place.getId(), user.getId());
                return PlaceDto.from(place, isBookmarked);
        }

        @Transactional
        public PlaceDetailDto updatePlace(Long placeId, UpdatePlaceDto updatePlaceDto) {
                Place place = placeRepository.findById(placeId)
                        .orElseThrow(()-> new BusinessException(PlaceErrorCode.NOT_FOUND));

                if (updatePlaceDto.getName()!=null) place.setName(updatePlaceDto.getName());
                if (updatePlaceDto.getAddress()!=null) place.setAddress(updatePlaceDto.getAddress());
                if (updatePlaceDto.getRegion1depth()!=null) place.setRegion1depth(updatePlaceDto.getRegion1depth());
                if (updatePlaceDto.getRegion2depth()!=null) place.setRegion2depth(updatePlaceDto.getRegion2depth());
                if (updatePlaceDto.getRegion3depth()!=null) place.setRegion3depth(updatePlaceDto.getRegion3depth());
                if (updatePlaceDto.getLatitude()!=null) place.setLatitude(updatePlaceDto.getLatitude());
                if (updatePlaceDto.getLongitude()!=null) place.setLongitude(updatePlaceDto.getLongitude());
                if (updatePlaceDto.getScore()!=null) place.setScore(updatePlaceDto.getScore());
                if(updatePlaceDto.getCategory()!=null){
                        Category category = categoryRepository.findByName(updatePlaceDto.getName());
                        place.setCategory(category);
                }

                placeRepository.save(place);

                return PlaceDetailDto.from(place, false, null);
        }

        @Transactional
        public String deletePlace(Long placeId) {
                Place place = placeRepository.findById(placeId)
                        .orElseThrow(()-> new BusinessException(PlaceErrorCode.NOT_FOUND));
                String result = place.getName();
                place.getAlbums().forEach(album -> {
                        album.setPlaceNameSnapshot(result);
                        albumRepository.save(album);
                }); // 장소가 삭제되어도 앨범에 장소 이름이 남음.
                placeRepository.delete(place);

                return result+" delete.";

        }
}
