package com.example.post.board.application.port.in;

import java.util.List;

public record ListPostsResult(
		List<PostSummaryResult> posts,
		int page,
		int size,
		long totalElements,
		int totalPages,
		boolean first,
		boolean last
) {
	public ListPostsResult {
		posts = List.copyOf(posts);
	}
}
