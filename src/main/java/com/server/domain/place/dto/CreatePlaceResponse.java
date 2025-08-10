package com.server.domain.place.dto;

import com.server.domain.category.dto.CategoryDto;
import com.server.domain.place.entity.Place;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreatePlaceResponse {

	@Schema(description = "장소 ID")
	private Long id;
	@Schema(description = "장소 이름")
	private String name;
	@Schema(description = "장소 주소")
	private String address;
	@Schema(description = "장소 카테고리")
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
