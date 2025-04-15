package com.server.domain.course.dto;

import com.server.domain.course.entity.Course;
import com.server.domain.place.dto.PlaceDto;
import com.server.domain.place.entity.Place;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDto {
    private String name;

    public static CourseDto from(Course course){
        return CourseDto.builder()
                .name(course.getName())
                .build();
    }
}
