package com.server.domain.user.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import java.util.HashMap;
import java.util.Map;

import com.server.domain.user.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.domain.user.entity.User;
import com.server.domain.user.service.UserService;
import com.server.global.jwt.JwtAuthentication;
import com.server.global.jwt.JwtService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({TestSecurityConfig.class, TestJpaConfig.class})
public class UserControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    private User mockUser;
    private UserDto mockUserDto;

    @BeforeEach
    void setUp() {
        // 각 테스트 전에 MockMvc를 재설정
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();

        // Setup mock User
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setNickname("testUser");
        mockUser.setThumbnail("thumbnail.jpg");
        mockUser.setCode("USER123");
        mockUser.setAdmin(false);

        // Setup mock UserDto
        mockUserDto = UserDto.builder().nickname("testUser").thumbnail("thumbnail.jpg")
                .code("USER123").isRegistered(true).restoreEnabled(false).build();

        // Setup userService to return mockUser
        when(userService.getUserWithPersonalInfo(any(Long.class))).thenReturn(mockUser);

        // Setup JWT service to return a valid token for any user ID
        when(jwtService.createAccessToken(anyLong())).thenReturn("mock-jwt-token");

        // 기본 인증 설정
        Authentication auth =
                new JwtAuthentication(mockUser, AuthorityUtils.createAuthorityList("ROLE_USER"));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("Get user information test")
    void getUserTest() throws Exception {
        // Setup mock userService behavior
        when(userService.getUser(any(User.class))).thenReturn(mockUserDto);

        // Execute request with JWT authentication and verify response
        mockMvc.perform(get("/api/users/me").with(JwtTestUtil.withJwt(jwtService, mockUser)))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Delete user (soft delete) test")
    void deleteUserTest() throws Exception {
        // Setup mock userService behavior
        when(userService.deleteUser(any(User.class), anyBoolean())).thenReturn("testUser");

        // Execute request with JWT authentication and verify response
        mockMvc.perform(delete("/api/users/me").with(JwtTestUtil.withJwt(jwtService, mockUser)))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Register user test")
    void registerUserTest() throws Exception {
        // Setup mock data
        Map<Long, Boolean> termAgreements = new HashMap<>();
        termAgreements.put(1L, true);
        termAgreements.put(2L, false);

        RegisterUserInDto registerUserInDto = new RegisterUserInDto();
        registerUserInDto.setTermAgreements(termAgreements);

        // Setup mock userService behavior
        when(userService.registerUser(any(User.class), anyMap())).thenReturn("testUser");

        // Execute request with JWT authentication and verify response
        mockMvc.perform(post("/api/users/me").with(JwtTestUtil.withJwt(jwtService, mockUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerUserInDto))).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Restore account test")
    void restoreAccountTest() throws Exception {
        // Setup mock data
        RestoreAccountDto restoreAccountDto = new RestoreAccountDto(true);

        // Setup mock userService behavior
        when(userService.restoreAccount(anyLong())).thenReturn("testUser");

        // Execute request with JWT authentication and verify response
        mockMvc.perform(post("/api/users/restore").with(JwtTestUtil.withJwt(jwtService, mockUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restoreAccountDto))).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Delete account (hard delete) test")
    void deleteAccountTest() throws Exception {
        // Setup mock data
        RestoreAccountDto restoreAccountDto = new RestoreAccountDto(false);

        // Setup mock userService behavior
        when(userService.deleteUser(any(User.class), anyBoolean())).thenReturn("testUser");

        // Execute request with JWT authentication and verify response
        mockMvc.perform(post("/api/users/restore").with(JwtTestUtil.withJwt(jwtService, mockUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restoreAccountDto))).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Set new profile test - valid nickname")
    void updateProfileTest() throws Exception {
        // Setup mock data
        ProfileUpdateDto profileUpdateDto = new ProfileUpdateDto(null, null);

        // Setup mock userService behavior
        when(userService.deleteUser(any(User.class), anyBoolean())).thenReturn("testUser");

        // Execute request with JWT authentication and verify response
        mockMvc.perform(put("/api/users/profile").with(JwtTestUtil.withJwt(jwtService, mockUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profileUpdateDto))).andDo(print())
            .andExpect(status().isBadRequest()) // 실패 기대
            .andExpect(jsonPath("$.message").value("닉네임은 필수 항목입니다."));
    }

    @Test
    @DisplayName("Set notifications - valid notification settings")
    void updateNotificationSettingsTest() throws Exception {
        // Setup mock data
        NotificationSettingsDto notificationSettingsDto = new NotificationSettingsDto(null, false);

        // Execute request with JWT authentication and verify response
        mockMvc.perform(put("/api/users/notifications/settings").with(JwtTestUtil.withJwt(jwtService, mockUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notificationSettingsDto))).andDo(print())
            .andExpect(status().isBadRequest()) // 실패 기대
            .andExpect(jsonPath("$.message").value("데이트 알림 설정은 필수 항목입니다."));
    }

}
