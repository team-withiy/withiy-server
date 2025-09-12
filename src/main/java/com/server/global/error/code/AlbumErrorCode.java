package com.server.global.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AlbumErrorCode implements ErrorCode {
	ALBUM_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "앨범을 찾을 수 없습니다."),
	PLACE_NOT_MATCHED(HttpStatus.BAD_REQUEST.value(), "요청한 장소와 사진의 장소가 일치하지 않습니다.");


	private final int status;
	private final String message;

}
