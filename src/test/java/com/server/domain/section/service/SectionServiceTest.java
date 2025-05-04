package com.server.domain.section.service;

import com.server.domain.category.dto.CategoryDto;
import com.server.domain.category.entity.Category;
import com.server.domain.category.repository.CategoryRepository;
import com.server.domain.section.dto.CreateSectionDto;
import com.server.domain.section.dto.HomeSectionDto;
import com.server.domain.section.dto.SectionDto;
import com.server.domain.section.entity.Section;
import com.server.domain.section.repository.SectionCourseRepository;
import com.server.domain.section.repository.SectionPlaceRepository;
import com.server.domain.section.repository.SectionRepository;
import com.server.global.error.code.CategoryErrorCode;
import com.server.global.error.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SectionServiceTest {

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private SectionPlaceRepository sectionPlaceRepository;

    @Mock
    private SectionCourseRepository sectionCourseRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private SectionService sectionService;

    @Test
    @DisplayName("홈 섹션 조회 테스트")
    void getHomeSections_shouldReturnAllHomeSections() {
        // given
        Category category = new Category("맛집", "restaurant_icon");
        ReflectionTestUtils.setField(category, "id", 1L);

        Section section1 = Section.builder().title("인기 맛집").type("place").uiType("horizontal")
                .sequence(1).category(category).isHome(true).build();
        ReflectionTestUtils.setField(section1, "id", 1L);

        Section section2 = Section.builder().title("추천 코스").type("course").uiType("horizontal")
                .sequence(2).category(category).isHome(true).build();
        ReflectionTestUtils.setField(section2, "id", 2L);

        List<Section> homeSections = Arrays.asList(section1, section2);

        when(sectionRepository.findByIsHome(true)).thenReturn(Optional.of(homeSections));
        when(sectionPlaceRepository.findAllBySectionId(anyLong()))
                .thenReturn(Optional.of(Collections.emptyList()));
        when(sectionCourseRepository.findAllBySectionId(anyLong()))
                .thenReturn(Optional.of(Collections.emptyList()));

        // when
        List<HomeSectionDto> result = sectionService.getHomeSections();

        // then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getTitle()).isEqualTo("인기 맛집");
        assertThat(result.get(0).getType()).isEqualTo("place");
        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(1).getTitle()).isEqualTo("추천 코스");
        assertThat(result.get(1).getType()).isEqualTo("course");

        verify(sectionRepository, times(1)).findByIsHome(true);
        verify(sectionPlaceRepository, times(1)).findAllBySectionId(1L);
        verify(sectionCourseRepository, times(1)).findAllBySectionId(2L);
    }

    @Test
    @DisplayName("홈 섹션이 없을 때 빈 리스트 반환 테스트")
    void getHomeSections_whenNoSections_shouldReturnEmptyList() {
        // given
        when(sectionRepository.findByIsHome(true)).thenReturn(Optional.of(Collections.emptyList()));

        // when
        List<HomeSectionDto> result = sectionService.getHomeSections();

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(sectionRepository, times(1)).findByIsHome(true);
        verify(sectionPlaceRepository, never()).findAllBySectionId(anyLong());
        verify(sectionCourseRepository, never()).findAllBySectionId(anyLong());
    }

    @Test
    @DisplayName("섹션 생성 테스트")
    void createSection_shouldReturnSectionDto() {
        // given
        Category category = new Category("맛집", "restaurant_icon");
        ReflectionTestUtils.setField(category, "id", 1L);

        CreateSectionDto createSectionDto = new CreateSectionDto();
        ReflectionTestUtils.setField(createSectionDto, "title", "새 맛집 섹션");
        ReflectionTestUtils.setField(createSectionDto, "type", "place");
        ReflectionTestUtils.setField(createSectionDto, "order", 3);
        ReflectionTestUtils.setField(createSectionDto, "categoryId", 1L);
        ReflectionTestUtils.setField(createSectionDto, "home", true);

        Section savedSection = Section.builder().title("새 맛집 섹션").type("place").uiType("horizontal")
                .sequence(3).category(category).isHome(true).build();
        ReflectionTestUtils.setField(savedSection, "id", 3L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(sectionRepository.save(any(Section.class))).thenReturn(savedSection);

        // when
        SectionDto result = sectionService.createSection(createSectionDto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("새 맛집 섹션");
        assertThat(result.getType()).isEqualTo("place");
        assertThat(result.getUiType()).isEqualTo("horizontal");
        assertThat(result.getCategoryDto().getId()).isEqualTo(1L);

        verify(categoryRepository, times(1)).findById(1L);
        verify(sectionRepository, times(1)).save(any(Section.class));
    }

    @Test
    @DisplayName("존재하지 않는 카테고리로 섹션 생성 시 예외 발생 테스트")
    void createSection_withNonExistentCategory_shouldThrowException() {
        // given
        CreateSectionDto createSectionDto = new CreateSectionDto();
        ReflectionTestUtils.setField(createSectionDto, "title", "새 맛집 섹션");
        ReflectionTestUtils.setField(createSectionDto, "type", "place");
        ReflectionTestUtils.setField(createSectionDto, "order", 3);
        ReflectionTestUtils.setField(createSectionDto, "categoryId", 999L); // 존재하지 않는 카테고리 ID
        ReflectionTestUtils.setField(createSectionDto, "home", true);

        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            sectionService.createSection(createSectionDto);
        });

        assertThat(exception.getErrorCode()).isEqualTo(CategoryErrorCode.NOT_FOUND);

        verify(categoryRepository, times(1)).findById(999L);
        verify(sectionRepository, never()).save(any(Section.class));
    }

    @Test
    @DisplayName("섹션을 DTO로 변환 테스트 - 장소 타입")
    void convertToSectionDto_withPlaceType_shouldConvertCorrectly() {
        // given
        Category category = new Category("맛집", "restaurant_icon");
        ReflectionTestUtils.setField(category, "id", 1L);

        Section section = Section.builder().title("인기 맛집").type("place").uiType("horizontal")
                .sequence(1).category(category).isHome(true).build();
        ReflectionTestUtils.setField(section, "id", 1L);

        when(sectionPlaceRepository.findAllBySectionId(1L))
                .thenReturn(Optional.of(Collections.emptyList()));

        // when
        SectionDto result = sectionService.convertToSectionDto(section);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("인기 맛집");
        assertThat(result.getType()).isEqualTo("place");
        assertThat(result.getUiType()).isEqualTo("horizontal");
        assertThat(result.getCategoryDto().getId()).isEqualTo(1L);
        assertThat(result.getCategoryDto().getName()).isEqualTo("맛집");
        assertThat(result.getPlaces()).isNotNull();
        assertThat(result.getCourses()).isNotNull();

        verify(sectionPlaceRepository, times(1)).findAllBySectionId(1L);
        verify(sectionCourseRepository, never()).findAllBySectionId(anyLong());
    }

    @Test
    @DisplayName("섹션을 DTO로 변환 테스트 - 코스 타입")
    void convertToSectionDto_withCourseType_shouldConvertCorrectly() {
        // given
        Category category = new Category("코스", "course_icon");
        ReflectionTestUtils.setField(category, "id", 2L);

        Section section = Section.builder().title("추천 코스").type("course").uiType("horizontal")
                .sequence(2).category(category).isHome(true).build();
        ReflectionTestUtils.setField(section, "id", 2L);

        when(sectionCourseRepository.findAllBySectionId(2L))
                .thenReturn(Optional.of(Collections.emptyList()));

        // when
        SectionDto result = sectionService.convertToSectionDto(section);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("추천 코스");
        assertThat(result.getType()).isEqualTo("course");
        assertThat(result.getUiType()).isEqualTo("horizontal");
        assertThat(result.getCategoryDto().getId()).isEqualTo(2L);
        assertThat(result.getCategoryDto().getName()).isEqualTo("코스");
        assertThat(result.getPlaces()).isNotNull();
        assertThat(result.getCourses()).isNotNull();

        verify(sectionPlaceRepository, never()).findAllBySectionId(anyLong());
        verify(sectionCourseRepository, times(1)).findAllBySectionId(2L);
    }
}
