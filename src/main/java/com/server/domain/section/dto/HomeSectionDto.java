package com.server.domain.section.dto;

import com.server.domain.category.dto.CategoryDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeSectionDto {
    private Long id;
    private String title;
    private String type;
    private int order;
    private String uiType;
    private CategoryDto category;
    private List<SectionPlaceDto> places;
    private List<SectionCourseDto> courses;
}
