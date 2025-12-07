package com.server.global.config.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.server.domain.search.entity.SearchTargetType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("SearchTargetType Converter 테스트")
class SearchTargetTypeConverterTest {

	private SearchTargetTypeConverter converter;

	@BeforeEach
	void setUp() {
		converter = new SearchTargetTypeConverter();
	}

	@Test
	@DisplayName("대문자 PLACE를 SearchTargetType.PLACE로 변환")
	void convert_upperCase_place() {
		// when
		SearchTargetType result = converter.convert("PLACE");

		// then
		assertThat(result).isEqualTo(SearchTargetType.PLACE);
	}

	@Test
	@DisplayName("소문자 place를 SearchTargetType.PLACE로 변환")
	void convert_lowerCase_place() {
		// when
		SearchTargetType result = converter.convert("place");

		// then
		assertThat(result).isEqualTo(SearchTargetType.PLACE);
	}

	@Test
	@DisplayName("대문자 ROUTE를 SearchTargetType.ROUTE로 변환")
	void convert_upperCase_route() {
		// when
		SearchTargetType result = converter.convert("ROUTE");

		// then
		assertThat(result).isEqualTo(SearchTargetType.ROUTE);
	}

	@Test
	@DisplayName("소문자 route를 SearchTargetType.ROUTE로 변환")
	void convert_lowerCase_route() {
		// when
		SearchTargetType result = converter.convert("route");

		// then
		assertThat(result).isEqualTo(SearchTargetType.ROUTE);
	}

	@Test
	@DisplayName("혼합 케이스 Place를 SearchTargetType.PLACE로 변환")
	void convert_mixedCase_place() {
		// when
		SearchTargetType result = converter.convert("Place");

		// then
		assertThat(result).isEqualTo(SearchTargetType.PLACE);
	}

	@Test
	@DisplayName("혼합 케이스 Route를 SearchTargetType.ROUTE로 변환")
	void convert_mixedCase_route() {
		// when
		SearchTargetType result = converter.convert("Route");

		// then
		assertThat(result).isEqualTo(SearchTargetType.ROUTE);
	}

	@ParameterizedTest
	@ValueSource(strings = {"PLACE", "place", "Place", "pLaCe"})
	@DisplayName("다양한 케이스의 place 입력값 처리")
	void convert_variousCases_place(String input) {
		// when
		SearchTargetType result = converter.convert(input);

		// then
		assertThat(result).isEqualTo(SearchTargetType.PLACE);
	}

	@ParameterizedTest
	@ValueSource(strings = {"ROUTE", "route", "Route", "rOuTe"})
	@DisplayName("다양한 케이스의 route 입력값 처리")
	void convert_variousCases_route(String input) {
		// when
		SearchTargetType result = converter.convert(input);

		// then
		assertThat(result).isEqualTo(SearchTargetType.ROUTE);
	}

	@Test
	@DisplayName("잘못된 값 입력 시 예외 발생")
	void convert_invalidValue_throwsException() {
		// when & then
		assertThatThrownBy(() -> converter.convert("INVALID"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("'INVALID'는 올바른 검색 대상이 아닙니다")
			.hasMessageContaining("PLACE, ROUTE");
	}

	@Test
	@DisplayName("빈 문자열 입력 시 예외 발생")
	void convert_emptyString_throwsException() {
		// when & then
		assertThatThrownBy(() -> converter.convert(""))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("올바른 검색 대상이 아닙니다");
	}

	@Test
	@DisplayName("숫자 입력 시 예외 발생")
	void convert_numericValue_throwsException() {
		// when & then
		assertThatThrownBy(() -> converter.convert("123"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("올바른 검색 대상이 아닙니다");
	}

	@Test
	@DisplayName("특수문자 포함 입력 시 예외 발생")
	void convert_specialCharacters_throwsException() {
		// when & then
		assertThatThrownBy(() -> converter.convert("PLACE@#$"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("올바른 검색 대상이 아닙니다");
	}

	@Test
	@DisplayName("공백 포함 입력 시 예외 발생")
	void convert_withSpaces_throwsException() {
		// when & then
		assertThatThrownBy(() -> converter.convert("PLACE ROUTE"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("올바른 검색 대상이 아닙니다");
	}

	@Test
	@DisplayName("course 오타 입력 시 예외 발생 - route가 맞음")
	void convert_course_throwsException() {
		// when & then
		assertThatThrownBy(() -> converter.convert("course"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("'course'는 올바른 검색 대상이 아닙니다")
			.hasMessageContaining("PLACE, ROUTE");
	}
}

