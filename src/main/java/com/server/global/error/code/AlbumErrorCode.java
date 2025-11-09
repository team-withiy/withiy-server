package com.server.global.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AlbumErrorCode implements ErrorCode {
    INVALID_PLACE_DATA(HttpStatus.BAD_REQUEST.value(), "placeId가 없으면 장소 이름, 주소, 위도, 경도 및 모든 지역 정보(1, 2, 3단계)는 필수입니다."),
    NOT_FOUND_ALBUM_FOR_SCHEDULE(HttpStatus.NOT_FOUND.value(), "일정과 연결된 앨범을 찾을 수 없습니다."),
	PLACE_NOT_MATCHED(HttpStatus.BAD_REQUEST.value(), "요청한 장소와 사진의 장소가 일치하지 않습니다."),
    ALBUM_COMMENT_OVER_10(HttpStatus.BAD_REQUEST.value(), "댓글은 한 사람당 최대 10개 까지 작성가능합니다.")
    ;


	private final int status;
	private final String message;

}
