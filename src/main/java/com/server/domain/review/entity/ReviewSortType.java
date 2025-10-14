package com.server.domain.review.entity;

import com.server.global.error.code.ReviewErrorCode;
import com.server.global.error.exception.BusinessException;

public enum ReviewSortType {
	SCORE, LATEST;

	public static ReviewSortType of(String sortBy) {
		if (sortBy == null) {
			return LATEST; // 기본값 지정
		}

		try {
			return valueOf(sortBy.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new BusinessException(ReviewErrorCode.INVALID_REVIEW_SORT_TYPE);
		}
	}
}
