package com.example.post.member.adapter.in.web;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "토큰 재발급 요청")
public record RefreshTokenRequest(
		@Schema(description = "refreshToken", example = "refresh.jwt.token")
		String refreshToken
) {
}
