package com.server.domain.admin.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ActiveContentsResponse {
    List<ActivePlaceDto> places;
    List<ActiveCourseDto> courses;

    @Builder
    public ActiveContentsResponse(List<ActivePlaceDto> places, List<ActiveCourseDto> courses) {
        this.places = places;
        this.courses = courses;
    }
}
