package com.example.post.board.application.port.in;

import java.util.List;

public record ListPostCommentsResult(
		List<CommentSummaryResult> items,
		int page,
		int size,
		long totalElements,
		int totalPages,
		boolean first,
		boolean last
) {
}
