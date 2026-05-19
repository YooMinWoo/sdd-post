package com.example.post.board.adapter.in.web;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "게시글 작성 응답")
public record CreatePostResponse(
		@Schema(description = "생성된 게시글 식별자", example = "1")
		Long id,

		@Schema(description = "저장된 게시글 제목", example = "안녕하세요")
		String title,

		@Schema(description = "저장된 게시글 본문", example = "첫 번째 게시글입니다.")
		String content,

		@Schema(description = "저장된 작성자명", example = "minu")
		String author,

		@Schema(description = "게시글 생성 시각", example = "2026-05-20T00:00:00Z")
		Instant createdAt
) {
}
