package com.example.post.member.adapter.in.web;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "토큰 응답")
public record TokenResponse(
		@Schema(description = "토큰 타입", example = "Bearer")
		String tokenType,

		@Schema(description = "accessToken", example = "access.jwt.token")
		String accessToken,

		@Schema(description = "refreshToken", example = "refresh.jwt.token")
		String refreshToken,

		@Schema(description = "accessToken 만료 시간(초)", example = "900")
		long expiresIn
) {
}
