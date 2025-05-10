package com.server.domain.category.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.domain.category.dto.CategoryDto;
import com.server.domain.category.dto.CreateCategoryDto;
import com.server.domain.category.service.CategoryService;
import com.server.domain.user.controller.JwtTestUtil;
import com.server.domain.user.controller.TestJpaConfig;
import com.server.domain.user.controller.TestSecurityConfig;
import com.server.domain.user.entity.User;
import com.server.global.dto.ApiResponseDto;
import com.server.global.error.exception.BusinessException;
import com.server.global.error.code.UserErrorCode;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
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
class CategoryControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

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
        mockUser.setAdmin(false);

        // JWT 서비스 설정
        when(jwtService.createAccessToken(anyLong())).thenReturn("mock-jwt-token");

        // 기본 인증 설정
        SecurityContextHolder.getContext().setAuthentication(
                new JwtAuthentication(mockUser, AuthorityUtils.createAuthorityList("ROLE_USER")));
    }

    @Test
    @DisplayName("카테고리 목록 조회 테스트")
    void getCategories_shouldReturnAllCategories() throws Exception {
        // given
        CategoryDto category1 = CategoryDto.builder().id(1L).name("카테고리1").icon("icon1").build();

        CategoryDto category2 = CategoryDto.builder().id(2L).name("카테고리2").icon("icon2").build();

        List<CategoryDto> categoryList = Arrays.asList(category1, category2);

        when(categoryService.getCategories()).thenReturn(categoryList);

        // when & then
        MvcResult result = mockMvc
                .perform(get("/api/categories").with(JwtTestUtil.withJwt(jwtService, mockUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andDo(print()).andReturn();

        String content = result.getResponse().getContentAsString();
        ApiResponseDto<List<CategoryDto>> response = objectMapper.readValue(content,
                objectMapper.getTypeFactory().constructParametricType(ApiResponseDto.class,
                        objectMapper.getTypeFactory().constructCollectionType(List.class,
                                CategoryDto.class)));

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getData()).hasSize(2);
        assertThat(response.getData().get(0).getId()).isEqualTo(1L);
        verify(categoryService, times(1)).getCategories();
    }

    @Test
    @DisplayName("카테고리 생성 테스트")
    void createCategory_shouldCreateAndReturnCategory() throws Exception {
        // given
        CreateCategoryDto createRequest =
                CreateCategoryDto.builder().name("새 카테고리").icon("new_icon").build();

        CategoryDto createdCategory =
                CategoryDto.builder().id(1L).name("새 카테고리").icon("new_icon").build();

        when(categoryService.createCategory(any(CreateCategoryDto.class)))
                .thenReturn(createdCategory);

        // when & then
        MvcResult result = mockMvc
                .perform(post("/api/categories").with(JwtTestUtil.withJwt(jwtService, mockUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk()).andDo(print()).andReturn();

        String content = result.getResponse().getContentAsString();
        ApiResponseDto<CategoryDto> response = objectMapper.readValue(content, objectMapper
                .getTypeFactory().constructParametricType(ApiResponseDto.class, CategoryDto.class));

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getData().getId()).isEqualTo(1L);
        verify(categoryService, times(1)).createCategory(any(CreateCategoryDto.class));
    }
}
