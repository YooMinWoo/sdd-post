package com.example.post.global.web;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.List;

@Schema(description = "표준 API 응답")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
		@Schema(description = "요청 성공 여부", example = "true")
		boolean success,

		@Schema(description = "실패 응답의 안정적인 에러 코드", example = "POST_TITLE_REQUIRED")
		String code,

		@Schema(description = "클라이언트에 표시할 메시지", example = "게시글이 생성되었습니다.")
		String message,

		@Schema(description = "응답 데이터")
		T data,

		@Schema(description = "요청 경로. 실패 응답에서 사용합니다.", example = "/posts")
		String path,

		@Schema(description = "응답 생성 시각", example = "2026-05-20T00:00:00Z")
		Instant timestamp,

		@ArraySchema(schema = @Schema(description = "필드별 에러 상세"))
		List<FieldError> errors
) {

	public static <T> ApiResponse<T> success(String message, T data) {
		return new ApiResponse<>(true, null, message, data, null, Instant.now(), List.of());
	}

	public static ApiResponse<Void> error(String code, String message, String path, Instant timestamp) {
		return error(code, message, path, timestamp, List.of());
	}

	public static ApiResponse<Void> error(
			String code,
			String message,
			String path,
			Instant timestamp,
			List<FieldError> errors
	) {
		return new ApiResponse<>(false, code, message, null, path, timestamp, List.copyOf(errors));
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
