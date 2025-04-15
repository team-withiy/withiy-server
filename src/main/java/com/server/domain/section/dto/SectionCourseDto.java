package com.server.domain.section.dto;

import com.server.domain.course.dto.CourseDto;
import com.server.domain.place.dto.PlaceDto;
import com.server.domain.section.entity.SectionCourse;
import com.server.domain.section.entity.SectionPlace;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionCourseDto {
    private SectionDto sectionDto;
    private CourseDto courseDto;
    private int sequence;

    public static SectionCourseDto from(SectionCourse sectionCourse, SectionDto sectionDto){
        return SectionCourseDto.builder()
                .sectionDto(sectionDto)
                .courseDto(CourseDto.from(sectionCourse.getCourse()))
                .sequence(sectionCourse.getSequence())
                .build();
    }
}
