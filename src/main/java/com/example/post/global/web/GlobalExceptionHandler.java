package com.example.post.global.web;

import com.example.post.global.exception.BusinessException;
import com.example.post.global.exception.ErrorCode;
import com.example.post.global.exception.GlobalErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Clock;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private final Clock clock;

	public GlobalExceptionHandler() {
		this(Clock.systemUTC());
	}

	public GlobalExceptionHandler(Clock clock) {
		this.clock = clock;
	}

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ApiResponse<Void>> handleBusinessException(
			BusinessException exception,
			HttpServletRequest request
	) {
		ErrorCode errorCode = exception.errorCode();
		return ResponseEntity.status(httpStatus(errorCode))
				.body(errorResponse(errorCode, request));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(
			IllegalArgumentException exception,
			HttpServletRequest request
	) {
		return ResponseEntity.badRequest()
				.body(errorResponse(GlobalErrorCode.INVALID_REQUEST, request));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpServletRequest request) {
		return ResponseEntity.badRequest()
				.body(errorResponse(GlobalErrorCode.MALFORMED_JSON, request));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleException(HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(errorResponse(GlobalErrorCode.INTERNAL_SERVER_ERROR, request));
	}

	private ApiResponse<Void> errorResponse(ErrorCode errorCode, HttpServletRequest request) {
		return ApiResponse.error(errorCode.code(), errorCode.description(), request.getRequestURI(), Instant.now(clock));
	}

	private static HttpStatus httpStatus(ErrorCode errorCode) {
		return switch (errorCode.code()) {
			case "DUPLICATE_EMAIL" -> HttpStatus.CONFLICT;
			case "POST_NOT_FOUND" -> HttpStatus.NOT_FOUND;
			case "POST_DELETE_FORBIDDEN" -> HttpStatus.FORBIDDEN;
			case "INVALID_CREDENTIALS", "INVALID_REFRESH_TOKEN", "UNAUTHORIZED", "INVALID_ACCESS_TOKEN" ->
					HttpStatus.UNAUTHORIZED;
			case "INVALID_REQUEST", "POST_TITLE_REQUIRED", "POST_TITLE_TOO_LONG", "COMMENT_CONTENT_REQUIRED",
					"COMMENT_CONTENT_TOO_LONG", "INVALID_EMAIL", "PASSWORD_REQUIRED", "PASSWORD_TOO_SHORT" ->
					HttpStatus.BAD_REQUEST;
			default -> HttpStatus.INTERNAL_SERVER_ERROR;
		};
	}
}
