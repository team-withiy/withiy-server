package com.server.global.constants;

/**
 * 페이지네이션 및 데이터 조회 시 사용되는 LIMIT 관련 상수를 중앙 집중 관리
 */
public final class PaginationConstants {

	private PaginationConstants() {
		throw new AssertionError("Cannot instantiate constants class");
	}

	/**
	 * Photo 관련 LIMIT
	 */
	public static final int PLACE_PHOTO_LIMIT = 10;
	public static final int REVIEW_PHOTO_LIMIT = 10;
	public static final int FOLDER_THUMBNAIL_LIMIT = 4;

	/**
	 * Review 관련 LIMIT
	 */
	public static final int PLACE_REVIEW_LIMIT = 10;
}
