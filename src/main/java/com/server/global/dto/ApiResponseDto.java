package com.server.global.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseDto<T> {

	private int status;
	private String message;
	private T data;
	private LocalDateTime timestamp;

	public static <T> ApiResponseDto<T> success(int status, T data) {
		return ApiResponseDto.<T>builder()
			.status(status)
			.message("Request processed successfully.")
			.data(data)
			.timestamp(LocalDateTime.now())
			.build();
	}

	public static <T> ApiResponseDto<T> error(int status, String message) {
		return ApiResponseDto.<T>builder()
			.status(status)
			.message(message)
			.timestamp(LocalDateTime.now())
			.build();
	}
}
