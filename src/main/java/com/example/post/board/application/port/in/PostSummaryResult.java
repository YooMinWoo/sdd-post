package com.example.post.board.application.port.in;

import java.time.Instant;

public record PostSummaryResult(
		Long id,
		String title,
		Long authorMemberId,
		String author,
		Instant createdAt
) {
}
