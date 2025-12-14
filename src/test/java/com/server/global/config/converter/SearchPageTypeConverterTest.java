package com.server.global.config.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.server.domain.search.entity.SearchPageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("SearchPageType Converter 테스트")
class SearchPageTypeConverterTest {

	private SearchPageTypeConverter converter;

	@BeforeEach
	void setUp() {
		converter = new SearchPageTypeConverter();
	}

	@Test
	@DisplayName("대문자 MAIN을 SearchPageType.MAIN으로 변환")
	void convert_upperCase_main() {
		// when
		SearchPageType result = converter.convert("MAIN");

		// then
		assertThat(result).isEqualTo(SearchPageType.MAIN);
	}

	@Test
	@DisplayName("소문자 main을 SearchPageType.MAIN으로 변환")
	void convert_lowerCase_main() {
		// when
		SearchPageType result = converter.convert("main");

		// then
		assertThat(result).isEqualTo(SearchPageType.MAIN);
	}

	@Test
	@DisplayName("대문자 DATE_SCHEDULE을 SearchPageType.DATE_SCHEDULE로 변환")
	void convert_upperCase_dateSchedule() {
		// when
		SearchPageType result = converter.convert("DATE_SCHEDULE");

		// then
		assertThat(result).isEqualTo(SearchPageType.DATE_SCHEDULE);
	}

	@Test
	@DisplayName("소문자 date_schedule을 SearchPageType.DATE_SCHEDULE로 변환")
	void convert_lowerCase_dateSchedule() {
		// when
		SearchPageType result = converter.convert("date_schedule");

		// then
		assertThat(result).isEqualTo(SearchPageType.DATE_SCHEDULE);
	}

	@Test
	@DisplayName("카멜케이스 dateSchedule을 SearchPageType.DATE_SCHEDULE로 변환")
	void convert_camelCase_dateSchedule() {
		// when
		SearchPageType result = converter.convert("dateSchedule");

		// then
		assertThat(result).isEqualTo(SearchPageType.DATE_SCHEDULE);
	}

	@ParameterizedTest
	@ValueSource(strings = {"MAIN", "main", "Main"})
	@DisplayName("다양한 케이스의 main 입력값 처리")
	void convert_variousCases_main(String input) {
		// when
		SearchPageType result = converter.convert(input);

		// then
		assertThat(result).isEqualTo(SearchPageType.MAIN);
	}

	@ParameterizedTest
	@ValueSource(strings = {"DATE_SCHEDULE", "date_schedule", "dateSchedule", "DateSchedule"})
	@DisplayName("다양한 케이스의 dateSchedule 입력값 처리")
	void convert_variousCases_dateSchedule(String input) {
		// when
		SearchPageType result = converter.convert(input);

		// then
		assertThat(result).isEqualTo(SearchPageType.DATE_SCHEDULE);
	}

	@Test
	@DisplayName("잘못된 값 입력 시 예외 발생")
	void convert_invalidValue_throwsException() {
		// when & then
		assertThatThrownBy(() -> converter.convert("INVALID"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("'INVALID'는 올바른 페이지 타입이 아닙니다")
			.hasMessageContaining("MAIN, DATE_SCHEDULE");
	}

	@Test
	@DisplayName("빈 문자열 입력 시 예외 발생")
	void convert_emptyString_throwsException() {
		// when & then
		assertThatThrownBy(() -> converter.convert(""))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("올바른 페이지 타입이 아닙니다");
	}

	@Test
	@DisplayName("숫자 입력 시 예외 발생")
	void convert_numericValue_throwsException() {
		// when & then
		assertThatThrownBy(() -> converter.convert("123"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("올바른 페이지 타입이 아닙니다");
	}

	@Test
	@DisplayName("특수문자 포함 입력 시 예외 발생")
	void convert_specialCharacters_throwsException() {
		// when & then
		assertThatThrownBy(() -> converter.convert("MAIN@#$"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("올바른 페이지 타입이 아닙니다");
	}
}

