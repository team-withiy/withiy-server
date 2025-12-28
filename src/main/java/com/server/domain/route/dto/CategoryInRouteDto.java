package com.server.domain.route.dto;

import com.server.domain.category.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryInRouteDto {

	private Long categoryId;
	private String name;
	private String iconUrl;

	public static CategoryInRouteDto from(Category category) {
		if (category == null) {
			return null;
		}
		return CategoryInRouteDto.builder()
			.categoryId(category.getId())
			.name(category.getName())
			.iconUrl(category.getIcon())
			.build();
	}

}
