package com.server.domain.section.service;

import com.server.domain.category.repository.CategoryRepository;
import com.server.domain.category.dto.CategoryDto;
import com.server.domain.category.entity.Category;
import com.server.domain.course.dto.CourseDto;
import com.server.domain.course.dto.CoursePlaceDto;
import com.server.domain.course.entity.Course;
import com.server.domain.place.dto.PlaceDto;
import com.server.domain.place.entity.Place;
import com.server.domain.section.dto.*;
import com.server.domain.section.entity.Section;
import com.server.domain.section.entity.SectionCourse;
import com.server.domain.section.entity.SectionPlace;
import com.server.domain.section.repository.SectionCourseRepository;
import com.server.domain.section.repository.SectionPlaceRepository;
import com.server.domain.section.repository.SectionRepository;
import com.server.global.error.code.CategoryErrorCode;
import com.server.global.error.exception.BusinessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    private final CategoryRepository categoryRepository;

    public List<HomeSectionDto> getHomeSections() {
        // 홈 섹션 조회
        List<Section> sections = sectionRepository.findByIsHome(true)
                .orElse(Collections.emptyList());

        // 섹션 목록을 DTO로 변환
        return sections.stream()
                .map(this::convertToHomeSectionDto)
                .collect(Collectors.toList());
    }

    public SectionDto convertToSectionDto(Section section) {
        List<PlaceDto> places = new ArrayList<>();
        List<CourseDto> courses = new ArrayList<>();

        if(section.getType().equals("place")) {
            places = sectionPlaceRepository.findAllBySectionId(section.getId())
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(sectionPlace -> PlaceDto.builder()
                            .name(sectionPlace.getPlace().getName())
                            .address(sectionPlace.getPlace().getAddress())
                            .latitude(sectionPlace.getPlace().getLatitude())
                            .longitude(sectionPlace.getPlace().getLongitude())
                            .categoryName(sectionPlace.getPlace().getCategory().getName())
                            .build())
                    .collect(Collectors.toList());
        } else if(section.getType().equals("course")) {
            courses = sectionCourseRepository.findAllBySectionId(section.getId())
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(sectionCourse -> CourseDto.builder()
                            .name(sectionCourse.getCourse().getName())
                            .build())
                    .collect(Collectors.toList());
        }

        CategoryDto categoryDto = CategoryDto.builder()
                .id(section.getCategory().getId())
                .name(section.getCategory().getName())
                .build();

        return SectionDto.builder()
                .title(section.getTitle())
                .type(section.getType())
                .uiType(section.getUiType())
                .categoryDto(categoryDto)
                .places(places)
                .courses(courses)
                .build();
    }

    public HomeSectionDto convertToHomeSectionDto(Section section) {
        List<SectionPlaceDto> places = new ArrayList<>();
        List<SectionCourseDto> courses = new ArrayList<>();


        if(section.getType().equals("place")) {
            places = sectionPlaceRepository.findAllBySectionId(section.getId())
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(sectionPlace -> SectionPlaceDto.builder()
                            .id(sectionPlace.getPlace().getId())
                            .name(sectionPlace.getPlace().getName())
                            .thumbnail(sectionPlace.getPlace().getThumbnail())
                            .address(sectionPlace.getPlace().getAddress())
                            .latitude(sectionPlace.getPlace().getLatitude())
                            .longitude(sectionPlace.getPlace().getLongitude())
                            .category(CategoryDto.from(sectionPlace.getPlace().getCategory()))
                            .score(sectionPlace.getPlace().getScore())
                            .order(sectionPlace.getSequence())
                            .build())
                    .collect(Collectors.toList());
        } else if(section.getType().equals("course")) {
            courses = sectionCourseRepository.findAllBySectionId(section.getId())
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(sectionCourse -> SectionCourseDto.builder()
                            .id(sectionCourse.getCourse().getId())
                            .name(sectionCourse.getCourse().getName())
                            .order(sectionCourse.getSequence())
                            .thumbnail(sectionCourse.getCourse().getThumbnail())
                            .score(sectionCourse.getCourse().getScore())
                            .places(sectionCourse.getCourse().getCoursePlaces().stream()
                                    .map(coursePlace -> CoursePlaceDto.builder()
                                            .category(CategoryDto.from(coursePlace.getPlace().getCategory()))
                                            .address(coursePlace.getPlace().getAddress())
                                            .build())
                                    .collect(Collectors.toList()))
                            .build())
                    .collect(Collectors.toList());
        }

        CategoryDto categoryDto = CategoryDto.builder()
                .id(section.getCategory().getId())
                .name(section.getCategory().getName())
                .icon(section.getCategory().getIcon())
                .build();

        return HomeSectionDto.builder()
                .id(section.getId())
                .title(section.getTitle())
                .type(section.getType())
                .order(section.getSequence())
                .uiType(section.getUiType())
                .category(categoryDto)
                .places(places)
                .courses(courses)
                .build();
    }


    /*@Transactional
    public SectionPlaceDto addSectionPlace(Section section, Place place) {
        // 현재 섹션에 대한 최대 sequence 값을 조회
        Integer maxSequence = sectionPlaceRepository.findMaxSequenceBySectionId(section.getId());

        SectionPlace sectionPlace = new SectionPlace();
        sectionPlace.setSection(section);
        sectionPlace.setPlace(place);
        sectionPlace.setSequence(maxSequence == null ? 0 : maxSequence + 1);
        SectionDto sectionDto = convertToSectionDto(section);
        sectionPlaceRepository.save(sectionPlace);

        return SectionPlaceDto.from(sectionPlace, sectionDto);
    }

    @Transactional
    public SectionCourseDto addSectionCourse(Section section, Course course) {
        // 현재 섹션에 대한 최대 sequence 값을 조회
        Integer maxSequence = sectionCourseRepository.findMaxSequenceBySectionId(section.getId());

        SectionCourse sectionCourse = new SectionCourse();
        sectionCourse.setSection(section);
        sectionCourse.setCourse(course);
        sectionCourse.setSequence(maxSequence == null ? 0 : maxSequence + 1);
        SectionDto sectionDto = convertToSectionDto(section);
        sectionCourseRepository.save(sectionCourse);

        return SectionCourseDto.from(sectionCourse, sectionDto);
    }*/

    @Transactional
    public SectionDto createSection(CreateSectionDto createSectionDto) {

        Category category = categoryRepository.findById(createSectionDto.getCategoryId())
                .orElseThrow(()-> new BusinessException(CategoryErrorCode.NOT_FOUND));
        Section section = Section.builder()
                .title(createSectionDto.getTitle())
                .type(createSectionDto.getType())
                .sequence(createSectionDto.getOrder())
                .category(category)
                .uiType("horizontal")
                .isHome(createSectionDto.isHome())
                .build();

        sectionRepository.save(section);
        return SectionDto.builder()
                .title(section.getTitle())
                .type(section.getType())
                .uiType(section.getUiType())
                .categoryDto(CategoryDto.from(category))
                .build();
    }
}

