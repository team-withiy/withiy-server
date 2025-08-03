package com.server.domain.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ActiveCourseDto {
    @Schema(description = "코스 고유 ID", example = "1")
    private Long courseId;
    @Schema(description = "코스 이름", example = "서울 데이트 코스")
    private String courseName;
    @Schema(description = "코스에 포함된 장소 이름 목록", example = "[\"서울숲\", \"한강공원\"]")
    private List<String> placeNames;
    @Schema(description = "북마크 수", example = "150")
    private Long bookmarkCount;
    @Schema(description = "코스 사진 URL 목록", example = "[\"https://example.com/course1.jpg\", \"https://example.com/course2.jpg\"]")
    private List <String> photoUrls;

    @Builder
    public ActiveCourseDto(Long courseId, String courseName, List<String> placeNames, Long bookmarkCount, List<String> photoUrls) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.placeNames = placeNames;
        this.bookmarkCount = bookmarkCount;
        this.photoUrls = photoUrls;
    }
}
