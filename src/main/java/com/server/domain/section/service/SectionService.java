package com.server.domain.section.service;

import com.server.domain.category.dto.CategoryDto;
import com.server.domain.course.dto.CourseDto;
import com.server.domain.place.dto.PlaceDto;
import com.server.domain.section.dto.SectionDto;
import com.server.domain.section.entity.Section;
import com.server.domain.section.repository.SectionCourseRepository;
import com.server.domain.section.repository.SectionPlaceRepository;
import com.server.domain.section.repository.SectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SectionService {

    private final SectionRepository sectionRepository;
    private final SectionPlaceRepository sectionPlaceRepository;
    private final SectionCourseRepository sectionCourseRepository;

    public List<SectionDto> getHomeSections() {
        // 홈 섹션 조회
        List<Section> sections = sectionRepository.findByIsHome(true)
                .orElse(Collections.emptyList());

        // 섹션 목록을 DTO로 변환
        return sections.stream()
                .map(this::convertToSectionDto)
                .collect(Collectors.toList());
    }

    private SectionDto convertToSectionDto(Section section) {
        List<PlaceDto> places = sectionPlaceRepository.findAllBySectionId(section.getId())
                .orElse(Collections.emptyList())
                .stream()
                .map(sectionPlace -> new PlaceDto(
                        sectionPlace.getPlace().getName(),
                        sectionPlace.getPlace().getAddress(),
                        sectionPlace.getPlace().getLatitude(),
                        sectionPlace.getPlace().getLongitude(),
                        sectionPlace.getPlace().getCategory().getId()
                ))
                .collect(Collectors.toList());

        List<CourseDto> courses = sectionCourseRepository.findAllBySectionId(section.getId())
                .orElse(Collections.emptyList())
                .stream()
                .map(sectionCourse -> new CourseDto(sectionCourse.getCourse().getName()))
                .collect(Collectors.toList());

        CategoryDto categoryDto = new CategoryDto(
                section.getCategory().getId(),
                section.getCategory().getName()
        );

        return new SectionDto(
                section.getTitle(),
                categoryDto,
                places,
                courses
        );
    }
}
