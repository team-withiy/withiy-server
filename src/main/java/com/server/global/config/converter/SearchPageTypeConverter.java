package com.server.global.config.converter;

import com.server.domain.search.entity.SearchPageType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * SearchPageType Enum 변환기
 * 대소문자 구분 없이 문자열을 SearchPageType으로 변환합니다.
 */
@Component
public class SearchPageTypeConverter implements Converter<String, SearchPageType> {

	@Override
	public SearchPageType convert(@NonNull String source) {
		try {
			// 카멜케이스를 스네이크케이스로 변환 후 대문자로 변환
			String normalized = source
				.replaceAll("([a-z])([A-Z])", "$1_$2")  // camelCase -> camel_Case
				.toUpperCase();  // CAMEL_CASE
			return SearchPageType.valueOf(normalized);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(
				String.format("'%s'는 올바른 페이지 타입이 아닙니다. 가능한 값: MAIN, DATE_SCHEDULE (또는 main, date_schedule, dateSchedule)", source));
		}
	}
}
