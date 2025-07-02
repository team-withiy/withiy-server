package com.server.global.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ProfileErrorCode implements ErrorCode {
    DOWNLOAD_PROFILE_IMAGE_FAILED(
        HttpStatus.NOT_FOUND.value(), "프로필 이미지 다운로드에 실패했습니다."),
    DOWNLOAD_PROFILE_IMAGE_EMPTY(
        HttpStatus.NOT_FOUND.value(), "프로필 이미지가 비어있습니다.");

    private final int status;
    private final String message;
}
