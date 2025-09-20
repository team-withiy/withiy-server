package com.server.domain.route.dto;

import com.server.domain.route.entity.Route;
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
public class CourseDto {

	private String name;

	public static CourseDto from(Route route) {
		return CourseDto.builder()
			.name(route.getName())
			.build();
	}
}
