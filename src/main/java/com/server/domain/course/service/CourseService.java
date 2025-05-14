package com.server.domain.course.service;

import com.server.domain.course.dto.CourseDetailDto;
import com.server.domain.course.dto.CourseImageDto;
import com.server.domain.course.entity.Course;
import com.server.domain.course.repository.CourseBookmarkRepository;
import com.server.domain.course.repository.CourseRepository;
import com.server.domain.place.dto.PlaceDetailDto;
import com.server.domain.place.repository.PlaceRepository;
import com.server.global.error.code.CourseErrorCode;
import com.server.global.error.code.PlaceErrorCode;
import com.server.global.error.exception.BusinessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseBookmarkRepository courseBookmarkRepository;
    private final PlaceRepository placeRepository;

    @Transactional
    public CourseDetailDto getCourseDetail(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(CourseErrorCode.NOT_FOUND));

        // 연관된 엔티티 로딩
        course.getCourseImages().size();
        course.getCoursePlaces().size();

        log.info("Course: {}", course);  // Course 객체의 상태 확인

        List<CourseImageDto> courseImages = course.getCourseImages().stream()
                .map(CourseImageDto::from)
                .collect(Collectors.toList());

        log.info("CourseImages: {}", courseImages);  // CourseImages 리스트 확인

        List<PlaceDetailDto> placeDetails = course.getCoursePlaces().stream()
                .map(c -> PlaceDetailDto.from(placeRepository.findById(c.getPlace().getId())
                        .orElseThrow(() -> new BusinessException(PlaceErrorCode.NOT_FOUND))))
                .collect(Collectors.toList());

        log.info("PlaceDetails: {}", placeDetails);  // PlaceDetails 리스트 확인

        log.info("정보", course.getName(), course.getThumbnail(), courseImages.get(0), placeDetails.get(0));
        return CourseDetailDto.builder()
                .name(course.getName())
                .thumbnail(course.getThumbnail())
                .courseImageDtos(courseImages)
                .placeDetailDtos(placeDetails)
                .build();
    }

}
