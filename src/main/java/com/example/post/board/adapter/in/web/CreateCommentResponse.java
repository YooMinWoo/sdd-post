package com.example.post.board.adapter.in.web;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "댓글 작성 응답")
public record CreateCommentResponse(
		@Schema(description = "생성된 댓글 식별자", example = "1")
		Long id
) {
}
