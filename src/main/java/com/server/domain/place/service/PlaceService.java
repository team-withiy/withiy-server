package com.server.domain.place.service;

import com.server.domain.place.dto.PlaceDetailDto;
import com.server.domain.place.dto.PlaceImageDto;
import com.server.domain.place.entity.Place;
import com.server.domain.place.entity.PlaceImage;
import com.server.domain.place.repository.PlaceRepository;
import com.server.global.dto.ImageResponseDto;
import com.server.global.error.code.PlaceErrorCode;
import com.server.global.error.exception.BusinessException;
import com.server.global.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlaceService {
        private final PlaceRepository placeRepository;
        private final ImageService imageService;

        public PlaceDetailDto getPlaceDetail(Long placeId) {
                Place place = placeRepository.findById(placeId)
                                .orElseThrow(() -> new BusinessException(PlaceErrorCode.NOT_FOUND));
                return PlaceDetailDto.builder().name(place.getName()).address(place.getAddress())
                                .latitude(place.getLatitude()).longitude(place.getLongitude())
                                .categoryName(place.getCategory().getName()).score(place.getScore())
                                .placeImageDtos(place.getPlaceImages().stream()
                                                .map(PlaceImageDto::from)
                                                .collect(Collectors.toList()))
                                .build();

        }

        /**
         * 장소 이미지 업로드
         * 
         * @param placeId 장소 ID
         * @param file 업로드할 이미지 파일
         * @return 업로드된 이미지 정보
         */
        @Transactional
        public PlaceImageDto uploadPlaceImage(Long placeId, MultipartFile file) {
                Place place = placeRepository.findById(placeId)
                                .orElseThrow(() -> new BusinessException(PlaceErrorCode.NOT_FOUND));

                // 공통 이미지 서비스를 사용하여 이미지 업로드
                ImageResponseDto imageResponseDto =
                                imageService.uploadImage(file, "place", placeId);

                // 장소 이미지 엔티티 생성 및 저장
                PlaceImage placeImage = new PlaceImage();
                placeImage.setImageUrl(imageResponseDto.getImageUrl());
                placeImage.setPlace(place);

                place.getPlaceImages().add(placeImage);
                placeRepository.save(place);

                log.info("Place image uploaded for place ID {}: {}", placeId,
                                imageResponseDto.getImageUrl());

                return PlaceImageDto.from(placeImage);
        }

        /**
         * 장소 대표 이미지(썸네일) 업데이트
         * 
         * @param placeId 장소 ID
         * @param file 업로드할 이미지 파일
         * @return 업데이트된 장소 정보
         */
        @Transactional
        public PlaceDetailDto updatePlaceThumbnail(Long placeId, MultipartFile file) {
                Place place = placeRepository.findById(placeId)
                                .orElseThrow(() -> new BusinessException(PlaceErrorCode.NOT_FOUND));

                // 기존 썸네일 이미지가 있으면 삭제
                String oldThumbnail = place.getThumbnail();
                if (oldThumbnail != null && !oldThumbnail.isEmpty()) {
                        imageService.deleteImage(oldThumbnail);
                }

                // 새 이미지 업로드
                ImageResponseDto imageResponseDto =
                                imageService.uploadImage(file, "place", placeId);

                // 장소 썸네일 업데이트
                place.setThumbnail(imageResponseDto.getImageUrl());
                placeRepository.save(place);

                log.info("Place thumbnail updated for place ID {}: {}", placeId,
                                imageResponseDto.getImageUrl());

                return getPlaceDetail(placeId);
        }
}
