package com.server.domain.section.dto;

import com.server.domain.category.dto.CategoryDto;
import com.server.domain.category.entity.Category;
import com.server.domain.course.dto.CourseDto;
import com.server.domain.course.entity.Course;
import com.server.domain.place.dto.PlaceDto;
import com.server.domain.place.entity.Place;
import com.server.domain.section.entity.SectionCourse;
import com.server.domain.section.entity.SectionPlace;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SectionDto {
    private String title;
    private CategoryDto categoryDto;
    private List<PlaceDto> places;
    private List<CourseDto> courses;
}
