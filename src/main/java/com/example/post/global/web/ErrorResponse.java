package com.example.post.global.web;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;

@Schema(description = "API 에러 응답")
public record ErrorResponse(
		@Schema(description = "에러 코드", example = "INVALID_REQUEST")
		String code,

		@Schema(description = "클라이언트에 표시할 에러 메시지", example = "요청 값이 올바르지 않습니다.")
		String message,

		@Schema(description = "요청 경로", example = "/posts")
		String path,

		@Schema(description = "에러 발생 시각", example = "2026-05-20T00:00:00Z")
		Instant timestamp,

		@ArraySchema(schema = @Schema(description = "필드별 에러 상세"))
		List<FieldError> errors
) {

	public static ErrorResponse of(String code, String message, String path, Instant timestamp) {
		return new ErrorResponse(code, message, path, timestamp, List.of());
	}

	@Schema(description = "필드별 에러 상세")
	public record FieldError(
			@Schema(description = "오류가 발생한 필드명", example = "title")
			String field,

			@Schema(description = "필드 오류 메시지", example = "제목은 필수입니다.")
			String message
	) {
	}
}
