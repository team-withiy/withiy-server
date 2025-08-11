package com.server.domain.term.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.server.domain.term.dto.TermDto;
import com.server.domain.term.service.TermService;
import com.server.domain.user.controller.TestJpaConfig;
import com.server.domain.user.controller.TestSecurityConfig;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({TestSecurityConfig.class, TestJpaConfig.class})
public class TermControllerTest {

	@Autowired
	private WebApplicationContext context;

	private MockMvc mockMvc;

	@MockBean
	private TermService termService;

	@BeforeEach
	void setUp() {
		// 각 테스트 전에 MockMvc를 재설정
		mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
	}

	@Test
	@DisplayName("약관 목록 조회 테스트")
	void getTermsTest() throws Exception {
		// Mock 데이터 준비
		List<String> content1 = Arrays.asList("내용1-1", "내용1-2");
		List<String> content2 = Arrays.asList("내용2-1", "내용2-2");

		TermDto term1 =
			TermDto.builder().id(1L).title("서비스 이용약관").content(content1).required(true).build();

		TermDto term2 = TermDto.builder().id(2L).title("개인정보 처리방침").content(content2).required(true)
			.build();

		List<TermDto> terms = Arrays.asList(term1, term2);

		// 서비스 Mock 설정
		when(termService.getAllTerms()).thenReturn(terms);

		// API 요청 실행 및 검증
		mockMvc.perform(get("/api/term")).andDo(print()).andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(200)).andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data.length()").value(2))
			.andExpect(jsonPath("$.data[0].id").value(1))
			.andExpect(jsonPath("$.data[0].title").value("서비스 이용약관"))
			.andExpect(jsonPath("$.data[0].content[0]").value("내용1-1"))
			.andExpect(jsonPath("$.data[0].required").value(true))
			.andExpect(jsonPath("$.data[1].id").value(2))
			.andExpect(jsonPath("$.data[1].title").value("개인정보 처리방침"));
	}
}
