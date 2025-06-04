package com.server.domain.course.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;


@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CourseDetailDto {
    private String name;
    private String thumbnail;
    private List<CourseImageDto> courseImageDtos;



}
