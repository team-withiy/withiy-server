package com.server.domain.place.dto;

import com.server.domain.category.dto.CategoryDto;
import com.server.domain.place.entity.Place;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreatePlaceResponse {
    private Long id;
    private String name;
    private String address;
    private CategoryDto category;

    @Builder
    public CreatePlaceResponse(Long id, String name, String address, CategoryDto category) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.category = category;
    }

    public static CreatePlaceResponse from(Place place) {
        return CreatePlaceResponse.builder()
                .id(place.getId())
                .name(place.getName())
                .address(place.getAddress())
                .category(CategoryDto.from(place.getCategory()))
                .build();
    }
}
