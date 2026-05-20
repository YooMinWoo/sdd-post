package com.example.post.board.adapter.in.web;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "게시글 작성 요청")
public record CreatePostRequest(
		@Schema(description = "게시글 제목. 앞뒤 공백은 제거되며 최대 100자까지 허용됩니다.", example = "안녕하세요")
	String title,

	@Schema(description = "게시글 본문. 앞뒤 공백은 제거되며 최대 5,000자까지 허용됩니다.", example = "첫 번째 게시글입니다.")
	String content
) {
}
