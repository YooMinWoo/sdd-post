package com.example.post.board.adapter.in.web;

import java.time.Instant;

public record UpdateCommentResponse(
		Long id,
		Long authorMemberId,
		String author,
		String content,
		Instant createdAt
) {
}
