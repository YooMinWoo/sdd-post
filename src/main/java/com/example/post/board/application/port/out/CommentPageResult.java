package com.example.post.board.application.port.out;

import com.example.post.board.domain.model.Comment;
import java.util.List;

public record CommentPageResult(
		List<Comment> comments,
		int page,
		int size,
		long totalElements,
		int totalPages,
		boolean first,
		boolean last
) {
}
