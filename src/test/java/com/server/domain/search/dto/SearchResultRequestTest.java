package com.server.domain.search.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.server.domain.search.dto.request.SearchResultRequest;
import com.server.domain.search.entity.SearchPageType;
import com.server.domain.search.entity.SearchTargetType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("검색 요청 DTO 검증 테스트")
class SearchResultRequestTest {

	private Validator validator;

	@BeforeEach
	void setUp() {
		try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
			validator = factory.getValidator();
		}
	}

	@Test
	@DisplayName("정상적인 검색 요청 - 메인 페이지, 장소 검색")
	void validRequest_mainPage_placeSearch() {
		// given
		SearchResultRequest request = new SearchResultRequest();
		request.setKeyword("홍대입구역");
		request.setPageType(SearchPageType.MAIN);
		request.setTargetType(SearchTargetType.PLACE);

		// when
		Set<ConstraintViolation<SearchResultRequest>> violations = validator.validate(request);

		// then
		assertThat(violations).isEmpty();
	}

	@Test
	@DisplayName("정상적인 검색 요청 - 일정 생성 페이지, 장소 검색")
	void validRequest_dateSchedulePage_placeSearch() {
		// given
		SearchResultRequest request = new SearchResultRequest();
		request.setKeyword("강남역");
		request.setPageType(SearchPageType.DATE_SCHEDULE);
		request.setTargetType(SearchTargetType.PLACE);

		// when
		Set<ConstraintViolation<SearchResultRequest>> violations = validator.validate(request);

		// then
		assertThat(violations).isEmpty();
	}

	@Test
	@DisplayName("정상적인 검색 요청 - 메인 페이지, 코스 검색")
	void validRequest_mainPage_routeSearch() {
		// given
		SearchResultRequest request = new SearchResultRequest();
		request.setKeyword("데이트 코스");
		request.setPageType(SearchPageType.MAIN);
		request.setTargetType(SearchTargetType.ROUTE);

		// when
		Set<ConstraintViolation<SearchResultRequest>> violations = validator.validate(request);

		// then
		assertThat(violations).isEmpty();
	}

	@Test
	@DisplayName("정상적인 검색 요청 - 일정 생성 페이지, 코스 검색")
	void validRequest_dateSchedulePage_routeSearch() {
		// given
		SearchResultRequest request = new SearchResultRequest();
		request.setKeyword("서울 코스");
		request.setPageType(SearchPageType.DATE_SCHEDULE);
		request.setTargetType(SearchTargetType.ROUTE);

		// when
		Set<ConstraintViolation<SearchResultRequest>> violations = validator.validate(request);

		// then
		assertThat(violations).isEmpty();
	}

	@Test
	@DisplayName("검색어가 없어도 유효한 요청")
	void validRequest_emptyKeyword() {
		// given
		SearchResultRequest request = new SearchResultRequest();
		request.setKeyword("");
		request.setPageType(SearchPageType.MAIN);
		request.setTargetType(SearchTargetType.PLACE);

		// when
		Set<ConstraintViolation<SearchResultRequest>> violations = validator.validate(request);

		// then
		assertThat(violations).isEmpty();
	}

	@Test
	@DisplayName("검색어가 null이어도 유효한 요청")
	void validRequest_nullKeyword() {
		// given
		SearchResultRequest request = new SearchResultRequest();
		request.setKeyword(null);
		request.setPageType(SearchPageType.MAIN);
		request.setTargetType(SearchTargetType.PLACE);

		// when
		Set<ConstraintViolation<SearchResultRequest>> violations = validator.validate(request);

		// then
		assertThat(violations).isEmpty();
	}

	@Test
	@DisplayName("페이지 타입이 null이면 검증 실패")
	void invalidRequest_nullPageType() {
		// given
		SearchResultRequest request = new SearchResultRequest();
		request.setKeyword("홍대입구역");
		request.setPageType(null);
		request.setTargetType(SearchTargetType.PLACE);

		// when
		Set<ConstraintViolation<SearchResultRequest>> violations = validator.validate(request);

		// then
		assertThat(violations).hasSize(1);
		assertThat(violations)
			.extracting(ConstraintViolation::getMessage)
			.containsExactly("검색 페이지는 필수입니다.");
	}

	@Test
	@DisplayName("검색 대상 타입이 null이면 검증 실패")
	void invalidRequest_nullTargetType() {
		// given
		SearchResultRequest request = new SearchResultRequest();
		request.setKeyword("홍대입구역");
		request.setPageType(SearchPageType.MAIN);
		request.setTargetType(null);

		// when
		Set<ConstraintViolation<SearchResultRequest>> violations = validator.validate(request);

		// then
		assertThat(violations).hasSize(1);
		assertThat(violations)
			.extracting(ConstraintViolation::getMessage)
			.containsExactly("검색 대상은 필수입니다.");
	}

	@Test
	@DisplayName("페이지 타입과 검색 대상 타입이 모두 null이면 검증 실패")
	void invalidRequest_bothTypesNull() {
		// given
		SearchResultRequest request = new SearchResultRequest();
		request.setKeyword("홍대입구역");
		request.setPageType(null);
		request.setTargetType(null);

		// when
		Set<ConstraintViolation<SearchResultRequest>> violations = validator.validate(request);

		// then
		assertThat(violations).hasSize(2);
		assertThat(violations)
			.extracting(ConstraintViolation::getMessage)
			.containsExactlyInAnyOrder("검색 페이지는 필수입니다.", "검색 대상은 필수입니다.");
	}

	@Test
	@DisplayName("모든 필드가 null이면 검증 실패")
	void invalidRequest_allFieldsNull() {
		// given
		SearchResultRequest request = new SearchResultRequest();

		// when
		Set<ConstraintViolation<SearchResultRequest>> violations = validator.validate(request);

		// then
		assertThat(violations).hasSize(2);
		assertThat(violations)
			.extracting(ConstraintViolation::getMessage)
			.containsExactlyInAnyOrder("검색 페이지는 필수입니다.", "검색 대상은 필수입니다.");
	}
}

