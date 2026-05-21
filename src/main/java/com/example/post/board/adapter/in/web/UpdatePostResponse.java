package com.example.post.board.adapter.in.web;

import java.time.Instant;

public record UpdatePostResponse(
		Long id,
		String title,
		String content,
		Long authorMemberId,
		String author,
		Instant createdAt,
		long commentCount
) {
}
