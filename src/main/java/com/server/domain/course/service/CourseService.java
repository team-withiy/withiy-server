package com.server.domain.course.service;

import com.server.domain.course.dto.CourseDto;
import com.server.domain.course.dto.CourseImageDto;
import com.server.domain.course.dto.CourseStatus;
import com.server.domain.course.entity.Course;
import com.server.domain.course.entity.CourseBookmark;
import com.server.domain.course.entity.CourseImage;
import com.server.domain.course.repository.CourseBookmarkRepository;
import com.server.domain.course.repository.CoursePlaceRepository;
import com.server.domain.course.repository.CourseRepository;

import com.server.domain.place.entity.Place;
import com.server.domain.search.dto.BookmarkedCourseDto;
import com.server.domain.user.entity.User;
import com.server.global.dto.ImageResponseDto;
import com.server.global.error.code.CourseErrorCode;
import com.server.global.error.exception.BusinessException;
import com.server.global.service.ImageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {

        private final CourseRepository courseRepository;
        private final CourseBookmarkRepository courseBookmarkRepository;
        private final CoursePlaceRepository coursePlaceRepository;
        private final ImageService imageService;

/*        @Transactional
        public CourseDetailDto getCourseDetail(Long courseId) {
                Course course = courseRepository.findById(courseId).orElseThrow(
                                () -> new BusinessException(CourseErrorCode.NOT_FOUND));

                // 연관된 엔티티 로딩
                course.getCourseImages().size();
                course.getCoursePlaces().size();

                log.info("Course: {}", course); // Course 객체의 상태 확인

                List<CourseImageDto> courseImages = course.getCourseImages().stream()
                                .map(CourseImageDto::from).collect(Collectors.toList());

                log.info("CourseImages: {}", courseImages); // CourseImages 리스트 확인

                List<PlaceDetailDto> placeDetails = course.getCoursePlaces().stream()
                                .map(c -> PlaceDetailDto.from(placeRepository
                                                .findById(c.getPlace().getId())
                                                .orElseThrow(() -> new BusinessException(
                                                                PlaceErrorCode.NOT_FOUND))))
                                .collect(Collectors.toList());

                log.info("PlaceDetails: {}", placeDetails); // PlaceDetails 리스트 확인

                log.info("정보", course.getName(), course.getThumbnail(), courseImages.get(0),
                                placeDetails.get(0));
                return CourseDetailDto.builder().name(course.getName())
                                .thumbnail(course.getThumbnail()).courseImageDtos(courseImages)
                                .placeDetailDtos(placeDetails).build();
        }*/

        /**
         * 코스 이미지 업로드
         * 
         * @param courseId 코스 ID
         * @param file 업로드할 이미지 파일
         * @return 업로드된 이미지 정보
         */
        @Transactional
        public CourseImageDto uploadCourseImage(Long courseId, MultipartFile file) {
                Course course = courseRepository.findById(courseId).orElseThrow(
                                () -> new BusinessException(CourseErrorCode.NOT_FOUND));

                // 공통 이미지 서비스를 사용하여 이미지 업로드
                ImageResponseDto imageResponseDto =
                                imageService.uploadImage(file, "course", courseId);

                // 코스 이미지 엔티티 생성 및 저장
                CourseImage courseImage = new CourseImage();
                courseImage.setImageUrl(imageResponseDto.getImageUrl());
                courseImage.setCourse(course);

//                course.getCourseImages().add(courseImage);
                courseRepository.save(course);

                log.info("Course image uploaded for course ID {}: {}", courseId,
                                imageResponseDto.getImageUrl());

                return CourseImageDto.builder().imageUrl(courseImage.getImageUrl()).build();
        }

        @Transactional
        public List<BookmarkedCourseDto> getBookmarkedCourses(User user) {
                return courseBookmarkRepository.findByUserWithCourse(user).stream()
                        .map(CourseBookmark::getCourse)
                        .map(BookmarkedCourseDto::from)
                        .collect(Collectors.toList());
        }

        /**
         * 코스 검색
         *
         * @param keyword 검색 키워드
         * @param user 사용자 정보
         * @return 검색된 코스 목록
         */
        @Transactional
        public List<CourseDto> searchCoursesByKeyword(String keyword, User user) {
                List<Course> courses = courseRepository.findByNameContainingIgnoreCase(keyword);
                return courses.stream()
                    .map(CourseDto::from)
                    .collect(Collectors.toList());
        }

        public List<Course> getActiveCoursesByKeyword(String keyword) {
                if (keyword == null || keyword.isEmpty()) {
                        return courseRepository.findCoursesByStatus(CourseStatus.ACTIVE);
                } else {
                        return courseRepository.findCoursesByStatusAndKeyword(CourseStatus.ACTIVE, keyword);
                }
        }

        @Transactional
        public long getBookmarkCount(Course course) {
                return courseBookmarkRepository.countByCourseAndNotDeleted(course);
        }

        public List<Place> getPlacesInCourse(Course course) {
                return coursePlaceRepository.findPlacesByCourse(course);
        }

        /**
         * 코스 대표 이미지(썸네일) 업데이트
         * 
         * @param courseId 코스 ID
         * @param file 업로드할 이미지 파일
         * @return 업데이트된 코스 정보
         */
        /*@Transactional
        public CourseDetailDto updateCourseThumbnail(Long courseId, MultipartFile file) {
                Course course = courseRepository.findById(courseId).orElseThrow(
                                () -> new BusinessException(CourseErrorCode.NOT_FOUND));

                // 기존 썸네일 이미지가 있으면 삭제
                String oldThumbnail = course.getThumbnail();
                if (oldThumbnail != null && !oldThumbnail.isEmpty()) {
                        imageService.deleteImage(oldThumbnail);
                }

                // 새 이미지 업로드
                ImageResponseDto imageResponseDto =
                                imageService.uploadImage(file, "course", courseId);

                // 코스 썸네일 업데이트
                course.setThumbnail(imageResponseDto.getImageUrl());
                courseRepository.save(course);

                log.info("Course thumbnail updated for course ID {}: {}", courseId,
                                imageResponseDto.getImageUrl());

                return getCourseDetail(courseId);
        }*/
}
