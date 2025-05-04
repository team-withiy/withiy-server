package com.server.domain.section.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.domain.category.dto.CategoryDto;
import com.server.domain.section.dto.CreateSectionDto;
import com.server.domain.section.dto.HomeSectionDto;
import com.server.domain.section.dto.SectionDto;
import com.server.domain.section.service.SectionService;
import com.server.domain.user.controller.JwtTestUtil;
import com.server.domain.user.controller.TestJpaConfig;
import com.server.domain.user.controller.TestSecurityConfig;
import com.server.domain.user.entity.User;
import com.server.global.dto.ApiResponseDto;
import com.server.global.jwt.JwtAuthentication;
import com.server.global.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({TestSecurityConfig.class, TestJpaConfig.class})
class SectionControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SectionService sectionService;

    @MockBean
    private JwtService jwtService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        // 각 테스트 전에 MockMvc를 재설정
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();

        // 테스트용 사용자 설정
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setNickname("testUser");
        mockUser.setThumbnail("thumbnail.jpg");
        mockUser.setCode("USER123");
        mockUser.setAdmin(true); // Admin 권한 부여 (섹션 생성에 필요)

        // JWT 서비스 설정
        when(jwtService.createAccessToken(anyLong())).thenReturn("mock-jwt-token");

        // 기본 인증 설정
        SecurityContextHolder.getContext().setAuthentication(
                new JwtAuthentication(mockUser, AuthorityUtils.createAuthorityList("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("홈 섹션 목록 조회 테스트")
    void getHomeSections_shouldReturnAllHomeSections() throws Exception {
        // given
        CategoryDto category =
                CategoryDto.builder().id(1L).name("맛집").icon("restaurant_icon").build();

        HomeSectionDto section1 = HomeSectionDto.builder().id(1L).title("인기 맛집").type("place")
                .order(1).uiType("horizontal").category(category).places(Collections.emptyList())
                .courses(Collections.emptyList()).build();

        HomeSectionDto section2 = HomeSectionDto.builder().id(2L).title("추천 코스").type("course")
                .order(2).uiType("horizontal").category(category).places(Collections.emptyList())
                .courses(Collections.emptyList()).build();

        List<HomeSectionDto> sectionList = Arrays.asList(section1, section2);

        when(sectionService.getHomeSections()).thenReturn(sectionList);

        // when & then
        MvcResult result = mockMvc
                .perform(get("/api/sections/home").contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk()).andDo(print()).andReturn();

        String content =
                result.getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8);
        ApiResponseDto<List<HomeSectionDto>> response = objectMapper.readValue(content,
                objectMapper.getTypeFactory().constructParametricType(ApiResponseDto.class,
                        objectMapper.getTypeFactory().constructCollectionType(List.class,
                                HomeSectionDto.class)));

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getData()).hasSize(2);
        assertThat(response.getData().get(0).getId()).isEqualTo(1L);
        assertThat(response.getData().get(1).getId()).isEqualTo(2L);
        verify(sectionService, times(1)).getHomeSections();
    }

    @Test
    @DisplayName("섹션 생성 테스트")
    void createSection_shouldCreateAndReturnSection() throws Exception {
        // given
        CategoryDto categoryDto = CategoryDto.builder().id(1L).name("맛집").build();

        CreateSectionDto createRequest = new CreateSectionDto();
        ReflectionTestUtils.setField(createRequest, "title", "새 맛집 섹션");
        ReflectionTestUtils.setField(createRequest, "type", "place");
        ReflectionTestUtils.setField(createRequest, "order", 3);
        ReflectionTestUtils.setField(createRequest, "categoryId", 1L);
        ReflectionTestUtils.setField(createRequest, "home", true);

        SectionDto createdSection = SectionDto.builder().title("새 맛집 섹션").type("place")
                .uiType("horizontal").categoryDto(categoryDto).places(Collections.emptyList())
                .courses(Collections.emptyList()).build();

        when(sectionService.createSection(any(CreateSectionDto.class))).thenReturn(createdSection);

        // when & then
        MvcResult result = mockMvc
                .perform(post("/api/sections").with(JwtTestUtil.withJwt(jwtService, mockUser))
                        .contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk()).andDo(print()).andReturn();

        String content =
                result.getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8);
        ApiResponseDto<SectionDto> response = objectMapper.readValue(content, objectMapper
                .getTypeFactory().constructParametricType(ApiResponseDto.class, SectionDto.class));

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getData().getTitle()).isEqualTo("새 맛집 섹션");
        assertThat(response.getData().getType()).isEqualTo("place");
        verify(sectionService, times(1)).createSection(any(CreateSectionDto.class));
    }
}
