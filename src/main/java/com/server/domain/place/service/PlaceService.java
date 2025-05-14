package com.server.domain.place.service;

import com.server.domain.place.dto.PlaceDetailDto;
import com.server.domain.place.dto.PlaceImageDto;
import com.server.domain.place.entity.Place;
import com.server.domain.place.repository.PlaceRepository;
import com.server.global.error.code.AuthErrorCode;
import com.server.global.error.code.ErrorCode;
import com.server.global.error.code.PlaceErrorCode;
import com.server.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlaceService {
    private final PlaceRepository placeRepository;

    public PlaceDetailDto getPlaceDetail(Long placeId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(()-> new BusinessException(PlaceErrorCode.NOT_FOUND));
        return PlaceDetailDto.builder()
                .name(place.getName())
                .address(place.getAddress())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .categoryName(place.getCategory().getName())
                .score(place.getScore())
                .placeImageDtos(place.getPlaceImages().stream()
                        .map(PlaceImageDto::from)
                        .collect(Collectors.toList()))
                .build();

    }
}
