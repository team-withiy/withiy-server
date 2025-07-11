package com.server.global.error.exception;

import com.server.global.error.code.ErrorCode;
import lombok.Getter;

@Getter
public class BadgeException extends BaseException{
    public BadgeException(ErrorCode errorCode) {
        super(errorCode);
    }
}
