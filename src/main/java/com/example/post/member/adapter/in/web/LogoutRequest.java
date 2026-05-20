package com.example.post.member.adapter.in.web;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그아웃 요청")
public record LogoutRequest(
		@Schema(description = "refreshToken", example = "refresh.jwt.token")
		String refreshToken
) {
}
