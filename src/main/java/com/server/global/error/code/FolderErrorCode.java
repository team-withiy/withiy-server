package com.server.global.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum FolderErrorCode implements ErrorCode {
	NOT_FOUND(HttpStatus.NOT_FOUND.value(), "Folder not found."),
	DUPLICATE_FOLDER_NAME(HttpStatus.CONFLICT.value(), "Duplicate folder name."),
	DUPLICATE_PLACE_IN_FOLDER(HttpStatus.CONFLICT.value(), "Place already exists in the folder.");


	private final int status;
	private final String message;
}
