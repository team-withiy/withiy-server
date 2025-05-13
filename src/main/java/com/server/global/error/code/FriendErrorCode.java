package com.server.global.error.code;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FriendErrorCode implements ErrorCode {
    FRIENDS_ALREADY_EXISTS(HttpStatus.CONFLICT.value(), "Already friends."),
    REQUEST_ALREADY_EXISTS(HttpStatus.CONFLICT.value(), "Friend request already sent."),
    REQUEST_ALREADY_REMOVED(HttpStatus.GONE.value(), "Friend request already removed."),
    REQUEST_ALREADY_REJECTED(HttpStatus.GONE.value(), "Friend request already rejected."),
    RECEIPT_ALREADY_EXISTS(HttpStatus.CONFLICT.value(), "Friend request already received from this user."),
    SELF_FRIEND_REQUEST_NOT_ALLOWED(HttpStatus.BAD_REQUEST.value(), "Cannot send friend request to oneself."),
    REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "Friend request not found.");

    private final int status;
    private final String message;
}
