package com.server.domain.search.service;

import com.server.domain.course.dto.CourseDto;
import com.server.domain.course.service.CourseService;
import com.server.domain.place.dto.PlaceDto;
import com.server.domain.place.service.PlaceService;
import com.server.domain.search.dto.*;
import com.server.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchFacadeService {
    private final SearchService searchService;
    private final PlaceService placeService;
    private final CourseService courseService;

    public SearchResponseDto search(User user, SearchRequestDto searchRequestDto) {

        String keyword = searchRequestDto.getKeyword();

        if(keyword == null || keyword.isBlank()){ // 검색어가 비어있을 경우 최근 검색어, 저장된 장소, 저장된 코스 반환
            return searchIfKeywordIsBlank(user);
        } else { // 검색어가 비어있지 않을 경우,
            return searchIfKeywordIsNotBlank(user, searchRequestDto);
        }
    }

    private SearchResponseDto searchIfKeywordIsNotBlank(User user, SearchRequestDto searchRequestDto) {
        String keyword = searchRequestDto.getKeyword();
        SearchSource source = searchRequestDto.getSource();

        List<PlaceDto> searchPlaces = placeService.searchPlacesByKeyword(source, keyword, user);
        List<CourseDto> searchCourses  = courseService.searchCoursesByKeyword(keyword, user);
        return SearchResponseDto.builder()
            .searchPlaces(searchPlaces)
            .searchCourses(searchCourses)
            .build();
    }

    private SearchResponseDto searchIfKeywordIsBlank(User user) {
        // 최근 검색어 조회
        List<SearchHistoryDto> recentKeywords = searchService.getRecentSearchHistory(user);
        List<BookmarkedPlaceDto> bookmarkedPlaces = placeService.getBookmarkedPlaces(user);
        List<BookmarkedCourseDto> bookmarkedCourses = courseService.getBookmarkedCourses(user);

        return SearchResponseDto.builder()
                .recentKeywords(recentKeywords)
                .bookmarkedPlaces(bookmarkedPlaces)
                .bookmarkedCourses(bookmarkedCourses)
                .build();
    }
}
