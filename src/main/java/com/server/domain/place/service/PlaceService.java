package com.server.domain.place.service;

import com.server.domain.category.dto.CategoryDto;
import com.server.domain.place.dto.PlaceDetailDto;
import com.server.domain.place.dto.PlaceDto;
import com.server.domain.place.dto.PlaceFocusDto;
import com.server.domain.place.entity.Place;
import com.server.domain.place.repository.PlaceBookmarkRepository;
import com.server.domain.place.repository.PlaceRepository;
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

        public PlaceDto getPlaceSimpleDetail(Long placeId) {
                Place place = placeRepository.findById(placeId)
                        .orElseThrow(()-> new BusinessException(PlaceErrorCode.NOT_FOUND));

                return PlaceDto.from(place, false);
        }

        public PlaceDto getPlaceSimpleDetailAfterLogin(Long placeId, Long userId) {
                Place place = placeRepository.findById(placeId)
                        .orElseThrow(()-> new BusinessException(PlaceErrorCode.NOT_FOUND));
                User user = userRepository.findById(userId)
                        .orElseThrow(()-> new BusinessException(UserErrorCode.NOT_FOUND));
                boolean isBookmarked = placeBookmarkRepository.existsByPlaceAndUser(place, user);
                return PlaceDto.from(place, isBookmarked);
        }

        public PlaceDetailDto getPlaceDetail(Long placeId) {
                Place place = placeRepository.findById(placeId)
                        .orElseThrow(()-> new BusinessException(PlaceErrorCode.NOT_FOUND));
                return PlaceDetailDto.from(place, false, s3UrlConfig);

        }

        public PlaceDetailDto getPlaceDetailAfterLogin(Long placeId, Long userId) {
                Place place = placeRepository.findById(placeId)
                        .orElseThrow(()-> new BusinessException(PlaceErrorCode.NOT_FOUND));
                User user = userRepository.findById(userId)
                        .orElseThrow(()-> new BusinessException(UserErrorCode.NOT_FOUND));
                boolean isBookmarked = placeBookmarkRepository.existsByPlaceAndUser(place, user);
                return PlaceDetailDto.from(place, isBookmarked, s3UrlConfig);
        }
}
