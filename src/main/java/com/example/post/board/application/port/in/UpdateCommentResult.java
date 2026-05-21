package com.example.post.board.application.port.in;

import java.time.Instant;

public record UpdateCommentResult(
		Long id,
		Long authorMemberId,
		String author,
		String content,
		Instant createdAt
) {
}
