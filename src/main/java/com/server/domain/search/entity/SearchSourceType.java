package com.server.domain.search.entity;

/**
 * 검색이 발생한 소스를 구분하는 타입.
 *
 * <ul>
 *   <li>{@link #MAIN} - 메인 페이지에서의 검색</li>
 *   <li>{@link #DATE_SCHEDULE} - 일정 페이지에서의 검색</li>
 * </ul>
 */
public enum SearchSourceType {
	MAIN,
	DATE_SCHEDULE
}
