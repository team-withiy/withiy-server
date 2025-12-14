package com.server.global.config.converter;

import com.server.domain.search.entity.SearchTargetType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * SearchTargetType Enum 변환기
 * 대소문자 구분 없이 문자열을 SearchTargetType으로 변환합니다.
 */
@Component
public class SearchTargetTypeConverter implements Converter<String, SearchTargetType> {

	@Override
	public SearchTargetType convert(@NonNull String source) {
		try {
			return SearchTargetType.valueOf(source.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(
				String.format("'%s'는 올바른 검색 대상이 아닙니다. 가능한 값: PLACE, ROUTE (또는 place, route)", source));
		}
	}
}
