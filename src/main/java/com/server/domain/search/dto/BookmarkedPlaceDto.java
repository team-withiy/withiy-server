package com.server.domain.search.dto;

import com.server.domain.place.entity.Place;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookmarkedPlaceDto {
    @Schema(description = "장소 ID", example = "1")
    private Long id;
    @Schema(description = "장소 이름", example = "홍대입구역")
    private String name;
    @Schema(description = "장소 주소", example = "서울특별시 마포구 양화로 123")
    private String address;
    @Schema(description = "장소 온도", example = "75")
    private Long score;

    public static BookmarkedPlaceDto from(Place place) {
        return BookmarkedPlaceDto.builder()
                .id(place.getId())
                .name(place.getName())
                .address(place.getAddress())
                .score(place.getScore())
                .build();
    }
}
