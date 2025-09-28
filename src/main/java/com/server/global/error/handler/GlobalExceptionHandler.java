package com.server.global.error.handler;

import com.server.global.dto.ApiResponseDto;
import com.server.global.error.exception.AuthException;
import com.server.global.error.exception.BusinessException;
import java.util.stream.Collectors;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<Object>> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDto.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler(AuthException.class)
	public ResponseEntity<ApiResponseDto<Object>> handleAuthException(AuthException e) {
		return ResponseEntity.status(e.getStatus())
			.body(ApiResponseDto.error(e.getStatus(), e.getMessage()));
	}

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ApiResponseDto<Object>> handleBusinessException(BusinessException e) {
		return ResponseEntity.status(e.getStatus())
			.body(ApiResponseDto.error(e.getStatus(), e.getMessage()));
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiResponseDto<Object>> handleAccessDeniedException(
		AccessDeniedException e) {
		HttpStatus status = HttpStatus.UNAUTHORIZED;
		return ResponseEntity.status(status)
			.body(ApiResponseDto.error(status.value(), e.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponseDto<Object>> handleValidationException(
		MethodArgumentNotValidException e) {
		String errorMessage = e.getBindingResult().getFieldErrors().stream()
			.map(DefaultMessageSourceResolvable::getDefaultMessage)
			.collect(Collectors.joining(", "));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(ApiResponseDto.error(HttpStatus.BAD_REQUEST.value(), errorMessage));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiResponseDto<Object>> handleJsonParseException(
		HttpMessageNotReadableException e) {
		String errorMessage = "요청 본문의 JSON 형식이 올바르지 않습니다.";
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(ApiResponseDto.error(HttpStatus.BAD_REQUEST.value(), errorMessage));
	}
}
