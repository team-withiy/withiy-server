package com.server.domain.search.dto;

import com.server.domain.place.entity.Place;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookmarkedPlaceDto {
    private Long id;
    private String name;
    private String address;
    private Long score;
    private String thumbnail;

    public static BookmarkedPlaceDto from(Place place) {
        return BookmarkedPlaceDto.builder()
                .id(place.getId())
                .name(place.getName())
                .address(place.getAddress())
                .score(place.getScore())
                .thumbnail(place.getThumbnail())
                .build();
    }
}
