package com.server.domain.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.domain.category.dto.CategoryDto;
import com.server.domain.dateSchedule.dto.DateSchedCreateRequest;
import com.server.domain.dateSchedule.dto.DateSchedPlaceDto;
import com.server.domain.dateSchedule.service.DateSchedFacade;
import com.server.domain.user.controller.JwtTestUtil;
import com.server.domain.user.controller.TestJpaConfig;
import com.server.domain.user.controller.TestSecurityConfig;
import com.server.domain.user.entity.User;
import com.server.global.dto.ApiResponseDto;
import com.server.global.jwt.JwtAuthentication;
import com.server.global.jwt.JwtService;
import java.util.List;
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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({TestSecurityConfig.class, TestJpaConfig.class})
class DateScheduleControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private DateSchedFacade dateSchedFacade;

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

    // TODO
//    @Test
//    @DisplayName("일정 등록 테스트")
//    void createDateScheduler() throws Exception {
//        DateSchedPlaceDto dateSchedPlaceDto = DateSchedPlaceDto.builder()
//                .name("서울숲")
//                .address("서울특별시 성동구 서울숲길 273")
//                .region1depth("서울특별시")
//                .region2depth("성동구")
//                .region3depth("서울숲동")
//                .latitude(37.5432)
//                .longitude(127.0423)
//                .build();
//        DateSchedCreateRequest request = DateSchedCreateRequest.builder()
//                .name("데이트 일정")
//                .scheduleAt("2025-12-25")
//                .places(List.of(dateSchedPlaceDto))
//                .build();
//
//        doNothing().when(dateSchedFacade).createDateSchedule(mockUser, request);
//
//        // when & then
//        MvcResult result = mockMvc
//                .perform(post("/api/data-scheduler").with(JwtTestUtil.withJwt(jwtService, mockUser))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk()).andDo(print()).andReturn();
//
//        String content = result.getResponse().getContentAsString();
//        ApiResponseDto<CategoryDto> response = objectMapper.readValue(content, objectMapper
//                .getTypeFactory().constructParametricType(ApiResponseDto.class, CategoryDto.class));
//
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        verify(dateSchedFacade, times(1)).createDateSchedule(mockUser, request);
//    }

}