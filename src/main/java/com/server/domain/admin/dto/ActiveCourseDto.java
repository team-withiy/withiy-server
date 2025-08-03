package com.server.domain.admin.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ActiveCourseDto {
    private Long courseId;
    private String courseName;
    private List<String> placeNames;
    private Long bookmarkCount;
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
