package com.server.domain.course.controller;

import com.server.domain.course.dto.CourseDetailDto;
import com.server.domain.course.service.CourseService;
import com.server.domain.user.entity.User;
import com.server.global.dto.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
    public ApiResponseDto<CourseDetailDto> getCourseDetail(@PathVariable Long courseId){
        CourseDetailDto courseDetailDto = courseService.getCourseDetail(courseId);
        log.info("Returning API response with CourseDetailDto: {}", courseDetailDto);

        return ApiResponseDto.success(HttpStatus.OK.value(), courseDetailDto);
    }

}
