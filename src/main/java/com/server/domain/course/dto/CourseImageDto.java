package com.server.domain.course.dto;

import com.server.domain.course.entity.CourseImage;
import com.server.domain.place.entity.PlaceImage;
import lombok.*;

@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class CourseImageDto {
    private String imageUrl;

    public static CourseImageDto from(CourseImage courseImage){
        return CourseImageDto.builder()
                .imageUrl(courseImage.getImageUrl())
                .build();
    }
}
