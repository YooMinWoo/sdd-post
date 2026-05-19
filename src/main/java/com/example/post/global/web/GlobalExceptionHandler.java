package com.example.post.global.web;

import com.example.post.member.application.exception.DuplicateEmailException;
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
	public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
			IllegalArgumentException exception,
			HttpServletRequest request
	) {
		return ResponseEntity.badRequest()
				.body(errorResponse("INVALID_REQUEST", exception.getMessage(), request));
	}

	@ExceptionHandler(DuplicateEmailException.class)
	public ResponseEntity<ErrorResponse> handleDuplicateEmailException(
			DuplicateEmailException exception,
			HttpServletRequest request
	) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(errorResponse("DUPLICATE_EMAIL", exception.getMessage(), request));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpServletRequest request) {
		return ResponseEntity.badRequest()
				.body(errorResponse("MALFORMED_JSON", "요청 본문을 읽을 수 없습니다.", request));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(errorResponse("INTERNAL_SERVER_ERROR", "서버 오류가 발생했습니다.", request));
	}

	private ErrorResponse errorResponse(String code, String message, HttpServletRequest request) {
		return ErrorResponse.of(code, message, request.getRequestURI(), Instant.now(clock));
	}
}
