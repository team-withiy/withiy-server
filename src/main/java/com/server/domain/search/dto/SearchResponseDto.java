package com.server.domain.search.dto;

import com.server.domain.course.dto.CourseDto;
import com.server.domain.place.dto.PlaceDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class SearchResponseDto {
    @Schema(description = "검색된 장소 목록")
    private List<PlaceDto> searchPlaces;
    @Schema(description = "검색된 코스 목록")
    private List<CourseDto> searchCourses;
    @Schema(description = "최근 검색어 목록")
    private List<SearchHistoryDto> recentKeywords;
    @Schema(description = "북마크된 장소 목록")
    private List<BookmarkedPlaceDto> bookmarkedPlaces;
    @Schema(description = "북마크된 코스 목록")
    private List<BookmarkedCourseDto> bookmarkedCourses;

    @Builder
    public SearchResponseDto(List<PlaceDto> searchPlaces, List<CourseDto> searchCourses,
                                 List<SearchHistoryDto> recentKeywords, List<BookmarkedPlaceDto> bookmarkedPlaces,
                                 List<BookmarkedCourseDto> bookmarkedCourses) {
        this.searchPlaces = searchPlaces;
        this.searchCourses = searchCourses;
        this.recentKeywords = recentKeywords;
        this.bookmarkedPlaces = bookmarkedPlaces;
        this.bookmarkedCourses = bookmarkedCourses;
    }
}
