package com.server.domain.section.dto;

import com.server.domain.course.dto.CourseDto;
import com.server.domain.course.dto.CoursePlaceDto;
import com.server.domain.place.dto.PlaceDto;
import com.server.domain.section.entity.SectionCourse;
import com.server.domain.section.entity.SectionPlace;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionCourseDto {
    private Long id;
    private String name;
    private int order;
    private String thumbnail;
    private Long score;
    private List<CoursePlaceDto> places;


}
