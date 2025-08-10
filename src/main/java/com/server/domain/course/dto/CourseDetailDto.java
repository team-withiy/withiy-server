package com.server.domain.course.dto;


import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CourseDetailDto {

	private String name;
	private String thumbnail;
	private List<CourseImageDto> courseImageDtos;


}
