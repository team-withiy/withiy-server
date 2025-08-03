package com.server.domain.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ActiveContentsResponse {
    @Schema(description = "운영 중인 장소 목록")
    private final List<ActivePlaceDto> places;
    @Schema(description = "운영 중인 코스 목록")
    private final List<ActiveCourseDto> courses;

    @Builder
    public ActiveContentsResponse(List<ActivePlaceDto> places, List<ActiveCourseDto> courses) {
        this.places = places;
        this.courses = courses;
    }
}
