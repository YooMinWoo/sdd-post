package com.example.post.board.adapter.in.web;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "댓글 작성 요청")
public record CreateCommentRequest(
		@Schema(description = "댓글 본문. 앞뒤 공백은 제거되며 최대 1,000자까지 허용됩니다.", example = "좋은 글입니다.")
		String content
) {
}
