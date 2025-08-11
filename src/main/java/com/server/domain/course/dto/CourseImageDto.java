package com.server.domain.course.dto;

import com.server.domain.course.entity.CourseImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class CourseImageDto {

	private String imageUrl;

	public static CourseImageDto from(CourseImage courseImage) {
		return CourseImageDto.builder()
			.imageUrl(courseImage.getImageUrl())
			.build();
	}
}
