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
import com.server.domain.place.entity.PlaceBookmark;
import com.server.domain.place.repository.PlaceBookmarkRepository;
import com.server.domain.place.repository.PlaceRepository;
import com.server.domain.review.entity.Review;
import com.server.domain.review.service.ReviewService;
import com.server.domain.search.dto.BookmarkedPlaceDto;
import com.server.domain.search.dto.SearchSource;
import com.server.domain.user.entity.User;
import com.server.domain.user.repository.UserRepository;
import com.server.global.error.code.CategoryErrorCode;
import com.server.global.error.code.PlaceErrorCode;
import com.server.global.error.code.UserErrorCode;
import com.server.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlaceService {
        private final PlaceRepository placeRepository;
        private final PlaceBookmarkRepository placeBookmarkRepository;
        private final UserRepository userRepository;
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
                return PlaceDetailDto.from(place, false);

        }

        @Transactional
        public PlaceDetailDto getPlaceDetailAfterLogin(Long placeId, Long userId) {
                Place place = placeRepository.findById(placeId)
                        .orElseThrow(()-> new BusinessException(PlaceErrorCode.NOT_FOUND));
                User user = userRepository.findById(userId)
                        .orElseThrow(()-> new BusinessException(UserErrorCode.NOT_FOUND));
                boolean isBookmarked = placeBookmarkRepository.existsByPlaceIdAndUserId(place.getId(), user.getId());
                return PlaceDetailDto.from(place, isBookmarked);
        }

        @Transactional
        public PlaceDto createPlace(CreatePlaceDto createPlaceDto) {
                Category category = categoryRepository.findByName(createPlaceDto.getCategory())
                    .orElseThrow(() -> new BusinessException(CategoryErrorCode.NOT_FOUND));
                Place place = new Place(createPlaceDto.getName(), createPlaceDto.getRegion1depth(),
                        createPlaceDto.getRegion2depth(), createPlaceDto.getRegion3depth(), createPlaceDto.getAddress(),
                        createPlaceDto.getLatitude(), createPlaceDto.getLongitude(), category);
                placeRepository.save(place);

                return PlaceDto.from(place, false);
        }

        @Transactional
        public PlaceDto createPlaceFirst(User user, CreatePlaceByUserDto createPlaceByUserDto) {

                // 1. 카테고리 조회
                Category category = categoryRepository.findByName(createPlaceByUserDto.getCategory())
                    .orElseThrow(() -> new BusinessException(CategoryErrorCode.NOT_FOUND));

                // 2. 장소 생성
                Place place = new Place(
                    createPlaceByUserDto.getPlaceName(),
                    createPlaceByUserDto.getAddress(),
                    createPlaceByUserDto.getLatitude(),
                    createPlaceByUserDto.getLongitude(),
                    category,
                    createPlaceByUserDto.getScore()
                );

                placeRepository.save(place);

                // 3. 리뷰 저장
                Review savedReview = reviewService.save(place, user, createPlaceByUserDto.getReview(), createPlaceByUserDto.getScore());

                // 4. 앨범 생성 및 저장
                Album album = new Album(createPlaceByUserDto.getPlaceName(), createPlaceByUserDto.getPlaceName(), user);
                Album savedAlbum = albumService.save(place, album); // 내부적으로 PlaceAlbum 생성 포함 가정

                // 5. 사진 생성 및 저장
                List<Photo> photos = createPlaceByUserDto.getPhotos().stream()
                    .map(photoDto -> Photo.builder()
                        .imgUrl(photoDto.getImgUrl())
                        .sequence(photoDto.getSequence())
                        .isPrivate(photoDto.isPrivate())
                        .place(place)
                        .album(savedAlbum)
                        .review(savedReview)
                        .build())
                    .toList();

                photoService.saveAll(photos);

                // 8. 북마크 여부 확인 및 DTO 반환
                boolean isBookmarked = placeBookmarkRepository.existsByPlaceIdAndUserId(place.getId(), user.getId());
                return PlaceDto.from(place, isBookmarked);
        }

        @Transactional
        public PlaceDto registerPlace(User user, RegisterPlaceDto registerPlaceDto) {
                Place place = placeRepository.findById(registerPlaceDto.getPlaceId())
                        .orElseThrow(()-> new BusinessException(PlaceErrorCode.NOT_FOUND));
                Review savedreview = reviewService.save(place, user, registerPlaceDto.getReview(),
                        registerPlaceDto.getScore());
                Album album = albumService.getAlbum(place);

                List<Photo> photos = registerPlaceDto.getPhotos().stream()
                        .map(photoDto -> Photo.builder()
                                .imgUrl(photoDto.getImgUrl())
                                .sequence(photoDto.getSequence())
                                .isPrivate(photoDto.isPrivate())
                                .place(place)
                                .album(album)
                                .review(savedreview)
                                .build())
                        .toList();

                photoService.saveAll(photos);
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
                if (updatePlaceDto.getLikeCount()!=null) place.setLikeCount(updatePlaceDto.getLikeCount());
                if(updatePlaceDto.getCategory()!=null){
                        Category category = categoryRepository.findByName(updatePlaceDto.getName())
                            .orElseThrow(() -> new BusinessException(CategoryErrorCode.NOT_FOUND));
                        place.setCategory(category);
                }

                placeRepository.save(place);

                return PlaceDetailDto.from(place, false);
        }

        @Transactional
        public String deletePlace(Long placeId) {
                Place place = placeRepository.findById(placeId)
                        .orElseThrow(()-> new BusinessException(PlaceErrorCode.NOT_FOUND));
                String result = place.getName();
                placeRepository.delete(place);
                return result+" delete.";
        }

        /**
         * 사용자가 북마크한 장소 목록을 조회합니다.
         * @param user 인증된 사용자 정보
         * @return 사용자가 북마크한 장소 목록
         */
        @Transactional
        public List<BookmarkedPlaceDto> getBookmarkedPlaces(User user) {

                List<PlaceBookmark> placeBookmarks = placeBookmarkRepository.findByUserWithPlace(user);

                return placeBookmarks.stream()
                    .map(PlaceBookmark::getPlace)
                    .map(BookmarkedPlaceDto::from)
                    .collect(Collectors.toList());
        }

        /**
         * 키워드로 장소를 검색합니다.
         * @param source 검색 소스 (MAIN, DATE_SCHEDULE 등)
         * @param keyword 검색 키워드
         * @param user 현재 사용자
         * @return 검색된 장소 목록
         */
        @Transactional
        public List<PlaceDto> searchPlacesByKeyword(SearchSource source, String keyword, User user) {

                // 검색 소스가 DATE_SCHEDULE인 경우, DB에 장소 정보가 없으면 네이버 검색 API를 호출하여 장소 정보를 가져옵니다.
                List<Place> places = placeRepository.findByNameContainingIgnoreCase(keyword);
                if(places.isEmpty() && source == SearchSource.DATE_SCHEDULE) {
                        // TODO : 네이버 검색 API를 호출하여 장소 정보를 가져오는 로직을 구현해야 합니다.
                }
                return places.stream()
                    .map(PlaceDto::from)
                    .collect(Collectors.toList());
        }

        public List<Place> getActivePlacesByCategoryAndKeyword(Category category, String keyword) {
                if (keyword == null || keyword.isBlank()) {
                        return placeRepository.findPlacesByStatusAndCategory(PlaceStatus.ACTIVE, category);
                }
                return placeRepository.findPlacesByStatusAndCategoryAndKeyword(PlaceStatus.ACTIVE, category, keyword);
        }

        public long getBookmarkCount(Place place) {
                return placeBookmarkRepository.countByPlaceAndNotDeleted(place);
        }
}
