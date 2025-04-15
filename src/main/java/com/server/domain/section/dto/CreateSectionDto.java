package com.server.domain.section.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.server.domain.category.dto.CategoryDto;
import com.server.domain.course.dto.CourseDto;
import com.server.domain.place.dto.PlaceDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CreateSectionDto {
    private String title;
    private String type;
    private Long categoryId;
    private boolean home;

}
