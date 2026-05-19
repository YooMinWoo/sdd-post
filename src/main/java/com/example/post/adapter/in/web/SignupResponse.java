package com.example.post.adapter.in.web;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "회원가입 응답")
public record SignupResponse(
		@Schema(description = "생성된 회원 식별자", example = "1")
		Long id,

		@Schema(description = "저장된 이메일", example = "minu@example.com")
		String email,

		@Schema(description = "저장된 닉네임", example = "minu")
		String nickname,

		@Schema(description = "회원 생성 시각", example = "2026-05-20T00:00:00Z")
		Instant createdAt
) {
}
