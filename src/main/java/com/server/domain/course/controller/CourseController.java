package com.server.domain.course.controller;

import com.server.domain.course.dto.CourseDetailDto;
import com.server.domain.course.dto.CourseImageDto;
import com.server.domain.course.service.CourseService;
import com.server.domain.user.entity.User;
import com.server.global.dto.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Slf4j
public class CourseController {
    private final CourseService courseService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{courseId}")
    @Operation(summary = "코스 정보 가져오기", description = "해당 id 코스 dto 반환")
    public ApiResponseDto<CourseDetailDto> getCourseDetail(@PathVariable Long courseId) {
        CourseDetailDto courseDetailDto = courseService.getCourseDetail(courseId);
        log.info("Returning API response with CourseDetailDto: {}", courseDetailDto);

        return ApiResponseDto.success(HttpStatus.OK.value(), courseDetailDto);
    }

    /**
     * 코스 이미지 업로드 API
     */
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/{courseId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "코스 이미지 업로드", description = "코스에 새로운 이미지 업로드")
    public ApiResponseDto<CourseImageDto> uploadCourseImage(@AuthenticationPrincipal User user,
            @PathVariable Long courseId, @RequestPart("file") MultipartFile file) {

        log.info("Course image upload requested for course ID: {}, by user: {}", courseId,
                user.getNickname());

        CourseImageDto courseImageDto = courseService.uploadCourseImage(courseId, file);
        return ApiResponseDto.success(HttpStatus.OK.value(), courseImageDto);
    }

    /**
     * 코스 대표 이미지(썸네일) 업데이트 API
     */
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/{courseId}/thumbnail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "코스 대표 이미지 업데이트", description = "코스의 대표 이미지(썸네일) 업데이트")
    public ApiResponseDto<CourseDetailDto> updateCourseThumbnail(@AuthenticationPrincipal User user,
            @PathVariable Long courseId, @RequestPart("file") MultipartFile file) {

        log.info("Course thumbnail update requested for course ID: {}, by user: {}", courseId,
                user.getNickname());

        CourseDetailDto courseDetailDto = courseService.updateCourseThumbnail(courseId, file);
        return ApiResponseDto.success(HttpStatus.OK.value(), courseDetailDto);
    }
}
