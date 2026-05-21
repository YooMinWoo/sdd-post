package com.example.post.board.application.port.in;

import java.time.Instant;

public record UpdatePostResult(
		Long id,
		String title,
		String content,
		Long authorMemberId,
		String author,
		Instant createdAt,
		long commentCount
) {
}
