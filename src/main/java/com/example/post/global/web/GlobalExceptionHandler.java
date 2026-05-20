package com.example.post.global.web;

import com.example.post.member.application.exception.DuplicateEmailException;
import com.example.post.member.application.exception.InvalidCredentialsException;
import com.example.post.member.application.exception.InvalidRefreshTokenException;
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

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(
			IllegalArgumentException exception,
			HttpServletRequest request
	) {
		return ResponseEntity.badRequest()
				.body(errorResponse("INVALID_REQUEST", request));
	}

	@ExceptionHandler(DuplicateEmailException.class)
	public ResponseEntity<ApiResponse<Void>> handleDuplicateEmailException(
			DuplicateEmailException exception,
			HttpServletRequest request
	) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(errorResponse("DUPLICATE_EMAIL", request));
	}

	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<ApiResponse<Void>> handleInvalidCredentialsException(
			InvalidCredentialsException exception,
			HttpServletRequest request
	) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(errorResponse("INVALID_CREDENTIALS", request));
	}

	@ExceptionHandler(InvalidRefreshTokenException.class)
	public ResponseEntity<ApiResponse<Void>> handleInvalidRefreshTokenException(
			InvalidRefreshTokenException exception,
			HttpServletRequest request
	) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(errorResponse("INVALID_REFRESH_TOKEN", request));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpServletRequest request) {
		return ResponseEntity.badRequest()
				.body(errorResponse("MALFORMED_JSON", request));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleException(HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(errorResponse("INTERNAL_SERVER_ERROR", request));
	}

	private ApiResponse<Void> errorResponse(String message, HttpServletRequest request) {
		return ApiResponse.error(message, request.getRequestURI(), Instant.now(clock));
	}
}
