package com.server.domain.search.dto;

import com.server.domain.course.entity.Course;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookmarkedCourseDto {
    private Long id;
    private String name;
    private Long score;
    private String thumbnailUrl;

    public static BookmarkedCourseDto from(Course course) {
        return BookmarkedCourseDto.builder()
                .id(course.getId())
                .name(course.getName())
                .score(course.getScore())
                .thumbnailUrl(course.getThumbnail())
                .build();
    }
}
