package com.server.domain.term.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.server.domain.term.dto.TermDto;
import com.server.domain.term.entity.Term;
import com.server.domain.term.repository.TermRepository;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class TermServiceTest {

	@Mock
	private TermRepository termRepository;

	@InjectMocks
	private TermService termService;

	private Term term1;
	private Term term2;
	private List<Term> termList;

	@BeforeEach
	void setUp() {
		// Mock 데이터 준비
		term1 = new Term(1L, "서비스 이용약관", "내용1-1||내용1-2", true);
		term2 = new Term(2L, "개인정보 처리방침", "내용2-1||내용2-2", true);
		termList = Arrays.asList(term1, term2);
	}

	@Test
	@DisplayName("모든 약관 조회 테스트")
	void getAllTermsTest() {
		// Mock 설정
		when(termRepository.findAll()).thenReturn(termList);

		// 서비스 메소드 호출
		List<TermDto> result = termService.getAllTerms();

		// 검증
		assertNotNull(result);
		assertEquals(2, result.size());

		// 첫 번째 약관 검증
		assertEquals(1L, result.get(0).getId());
		assertEquals("서비스 이용약관", result.get(0).getTitle());
		assertEquals(2, result.get(0).getContent().size());
		assertEquals("내용1-1", result.get(0).getContent().get(0));
		assertEquals("내용1-2", result.get(0).getContent().get(1));
		assertEquals(true, result.get(0).isRequired());

		// 두 번째 약관 검증
		assertEquals(2L, result.get(1).getId());
		assertEquals("개인정보 처리방침", result.get(1).getTitle());
		assertEquals(2, result.get(1).getContent().size());
		assertEquals("내용2-1", result.get(1).getContent().get(0));
		assertEquals("내용2-2", result.get(1).getContent().get(1));
		assertEquals(true, result.get(1).isRequired());
	}
}
