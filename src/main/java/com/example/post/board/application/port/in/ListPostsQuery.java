package com.example.post.board.application.port.in;

public record ListPostsQuery(Integer page, Integer size, String keyword) {

	public ListPostsQuery(Integer page, Integer size) {
		this(page, size, null);
	}
}
