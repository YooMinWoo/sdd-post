package com.example.post.board.adapter.out.persistence;

interface CommentCountProjection {

	Long getPostId();

	Long getCommentCount();
}
