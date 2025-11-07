package com.server.domain.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.domain.user.dto.CoupleConnectionRequestDto;
import com.server.domain.user.dto.CoupleDto;
import com.server.domain.user.dto.FirstMetDateUpdateDto;
import com.server.domain.user.entity.Role;
import com.server.domain.user.entity.User;
import com.server.domain.user.service.CoupleService;
import com.server.global.jwt.JwtAuthentication;
import com.server.global.jwt.JwtService;
import java.time.LocalDate;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({TestSecurityConfig.class, TestJpaConfig.class})
public class CoupleControllerTest {

	@Autowired
	private WebApplicationContext context;

	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private CoupleService coupleService;

	@MockBean
	private JwtService jwtService;

	private User mockUser;
	private CoupleDto mockCoupleDto;

	@BeforeEach
	void setUp() {
		// 각 테스트 전에 MockMvc를 재설정
		mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();

		// Setup mock User
		mockUser = new User();
		ReflectionTestUtils.setField(mockUser, "id", 1L);
		ReflectionTestUtils.setField(mockUser, "nickname", "testUser");
		ReflectionTestUtils.setField(mockUser, "thumbnail", "thumbnail.jpg");
		ReflectionTestUtils.setField(mockUser, "code", "USER123");
		ReflectionTestUtils.setField(mockUser, "role", Role.ROLE_USER);

		// Setup mock CoupleDto
		mockCoupleDto = CoupleDto.builder().id(1L).partnerNickname("partnerName")
			.partnerThumbnail("partner_thumbnail.jpg").firstMetDate(LocalDate.of(2025, 1, 1))
			.build();

		// Setup JWT service to return a valid token for any user ID
		when(jwtService.createAccessToken(any(Long.class))).thenReturn("mock-jwt-token");

		// 기본 인증 설정
		Authentication auth =
			new JwtAuthentication(mockUser, AuthorityUtils.createAuthorityList("ROLE_USER"));
		SecurityContextHolder.getContext().setAuthentication(auth);
	}

	@Test
	@DisplayName("Connect couple test")
	void connectCoupleTest() throws Exception {
		// Setup mock data
		CoupleConnectionRequestDto requestDto = new CoupleConnectionRequestDto();
		requestDto.setPartnerCode("PARTNER_CODE");
		requestDto.setFirstMetDate(LocalDate.of(2025, 1, 1));

		// Setup mock coupleService behavior
		when(coupleService.connectCouple(any(User.class), any(String.class), any(LocalDate.class)))
			.thenReturn(mockCoupleDto);

		// Execute request with JWT authentication and verify response
		mockMvc.perform(post("/api/couples").with(JwtTestUtil.withJwt(jwtService, mockUser))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto))).andDo(print())
			.andExpect(status().isCreated()).andExpect(jsonPath("$.data.id").value(1))
			.andExpect(jsonPath("$.data.partnerNickname").value("partnerName"));
	}

	@Test
	@DisplayName("Validate couple first met date")
	void validateFirstMetDateTest() throws Exception {
		// Setup mock data
		CoupleConnectionRequestDto requestDto = new CoupleConnectionRequestDto();
		requestDto.setPartnerCode("PARTNER_CODE");
		requestDto.setFirstMetDate(LocalDate.now().plusDays(1)); // 미래 날짜

		// Execute request with JWT authentication and verify response
		mockMvc.perform(post("/api/couples").with(JwtTestUtil.withJwt(jwtService, mockUser))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto))).andDo(print())
			.andExpect(status().isBadRequest()) // 실패 기대
			.andExpect(jsonPath("$.message").value("처음 만난 날은 오늘 이전이어야 합니다."));
	}

	@Test
	@DisplayName("Get couple information test")
	void getCoupleTest() throws Exception {
		// Setup mock coupleService behavior
		when(coupleService.getCouple(any(User.class))).thenReturn(mockCoupleDto);

		// Execute request with JWT authentication and verify response
		mockMvc.perform(get("/api/couples").with(JwtTestUtil.withJwt(jwtService, mockUser)))
			.andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value(1))
			.andExpect(jsonPath("$.data.partnerNickname").value("partnerName"));
	}

	@Test
	@DisplayName("Disconnect couple test")
	void disconnectCoupleTest() throws Exception {
		// Setup mock coupleService behavior
		when(coupleService.disconnectCouple(any(User.class))).thenReturn(1L);

		// Execute request with JWT authentication and verify response
		mockMvc.perform(delete("/api/couples").with(JwtTestUtil.withJwt(jwtService, mockUser)))
			.andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data").value(1));
	}

	@Test
	@DisplayName("Update first met date test")
	void updateFirstMetDateTest() throws Exception {
		// Setup mock data
		FirstMetDateUpdateDto requestDto = new FirstMetDateUpdateDto();
		requestDto.setFirstMetDate(LocalDate.of(2024, 12, 25));

		// Setup mock coupleService behavior
		when(coupleService.updateFirstMetDate(any(User.class), any(LocalDate.class)))
			.thenReturn(mockCoupleDto);

		// Execute request with JWT authentication and verify response
		mockMvc.perform(
				patch("/api/couples/first-met-date").with(JwtTestUtil.withJwt(jwtService, mockUser))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDto)))
			.andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value(1))
			.andExpect(jsonPath("$.data.partnerNickname").value("partnerName"));
	}
}
