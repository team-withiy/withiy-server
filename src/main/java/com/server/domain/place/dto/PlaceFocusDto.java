package com.server.domain.place.dto;

import com.server.domain.category.dto.CategoryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceFocusDto {

	private Long id;
	private String name;
	private CategoryDto category;
}
