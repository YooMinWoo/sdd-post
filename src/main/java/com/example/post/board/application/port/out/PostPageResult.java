package com.example.post.board.application.port.out;

import com.example.post.board.domain.model.Post;
import java.util.List;

public record PostPageResult(
		List<Post> posts,
		int page,
		int size,
		long totalElements,
		int totalPages,
		boolean first,
		boolean last
) {
	public PostPageResult {
		posts = List.copyOf(posts);
	}
}
