package com.server.domain.search.dto;

import com.server.domain.course.entity.Course;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookmarkedCourseDto {
    @Schema(description = "코스 ID", example = "1")
    private Long id;
    @Schema(description = "코스 이름", example = "홍대 데이트 코스")
    private String name;
    @Schema(description = "코스 온도", example = "81")
    private Long score;
    @Schema(description = "코스 썸네일 URL", example = "https://example.com/thumbnail.jpg")
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
