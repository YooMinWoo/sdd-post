package com.example.post.member.application.port.in;

public record TokenResult(
		String tokenType,
		String accessToken,
		String refreshToken,
		long expiresIn
) {
}
