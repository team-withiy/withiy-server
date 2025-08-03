package com.server.domain.admin.service;

import com.server.domain.admin.dto.ActiveContentsResponse;
import com.server.domain.admin.dto.ActiveCourseDto;
import com.server.domain.admin.dto.ActivePlaceDto;
import com.server.domain.album.entity.Album;
import com.server.domain.album.service.AlbumService;
import com.server.domain.category.dto.CategoryDto;
import com.server.domain.category.entity.Category;
import com.server.domain.category.service.CategoryService;
import com.server.domain.course.entity.Course;
import com.server.domain.course.service.CourseService;
import com.server.domain.photo.service.PhotoService;
import com.server.domain.place.entity.Place;
import com.server.domain.place.service.PlaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminFacade {
    private final PlaceService placeService;
    private final CategoryService categoryService;
    private final AlbumService albumService;
    private final PhotoService photoService;
    private final CourseService courseService;

    @Transactional(readOnly = true)
    public ActiveContentsResponse getActiveContents(String categoryName, String keyword) {

        // 카테고리 이름으로 카테고리 조회
        Category category = categoryService.getCategoryByName(categoryName);

        // 카테고리와 키워드로 활성화된 장소 조회
        List<Place> places = placeService.getActivePlacesByCategoryAndKeyword(category, keyword);

        // ActivePlaceDto 리스트 생성
        List<ActivePlaceDto> activePlaces = getActivePlaces(places, category, keyword);

        // ActiveCourseDto 리스트 생성
        List<ActiveCourseDto> activeCourses = getActiveCourses(keyword);

        // ActiveContentsResponse
        return ActiveContentsResponse.builder()
                .places(activePlaces)
                .courses(activeCourses)
                .build();
    }

    private List<ActiveCourseDto> getActiveCourses(String keyword) {
        return courseService.getActiveCoursesByKeyword(keyword)
            .stream()
            .map(this::convertToActiveCourseDto)
            .toList();
    }

    private ActiveCourseDto convertToActiveCourseDto(Course course) {
        List<String> placeNames = new ArrayList<>();
        List<String> photoUrls = new ArrayList<>();
        List<Place> places = courseService.getPlacesInCourse(course);

        for (Place place : places) {
            placeNames.add(place.getName());
            Album album = albumService.getAlbum(place);
            photoUrls.addAll(photoService.getPhotoUrls(album));
        }

        return ActiveCourseDto.builder()
            .courseId(course.getId())
            .courseName(course.getName())
            .placeNames(placeNames)
            .bookmarkCount(courseService.getBookmarkCount(course))
            .photoUrls(photoUrls)
            .build();
    }

    List<ActivePlaceDto> getActivePlaces(List<Place> places, Category category, String keyword) {
        // 각 Place에 대해 북마크 수, 좋아요 수, 이미지 URL 목록을 조회하여 ActivePlaceDto 생성
        List<ActivePlaceDto> activePlaces = new ArrayList<>();
        for( Place place : places) {
            long bookmarkCount  = placeService.getBookmarkCount(place);
            long likeCount = place.getLikeCount();

            // 사진 URL 목록 조회
            Album album = albumService.getAlbum(place);
            List<String> photoUrls = photoService.getPhotoUrls(album);

            activePlaces.add(ActivePlaceDto.builder()
                .placeId(place.getId())
                .placeName(place.getName())
                .placeAddress(place.getAddress())
                .createdByAdmin(place.isCreatedByAdmin())
                .bookmarkCount(bookmarkCount)
                .likeCount(likeCount)
                .photoUrls(photoUrls)
                .placeCategory(CategoryDto.from(category))
                .build());
        }
        return activePlaces;
    }
}
