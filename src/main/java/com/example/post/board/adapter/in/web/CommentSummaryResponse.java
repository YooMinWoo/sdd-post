package com.example.post.board.adapter.in.web;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "댓글 요약 응답")
public record CommentSummaryResponse(
		@Schema(description = "댓글 식별자", example = "1")
		Long id,

		@Schema(description = "댓글 작성자 회원 식별자", example = "2")
		Long authorMemberId,

		@Schema(description = "댓글 작성자 현재 닉네임", example = "minu")
		String author,

		@Schema(description = "댓글 본문", example = "좋은 글입니다.")
		String content,

		@Schema(description = "댓글 생성 시각", example = "2026-05-21T00:00:00Z")
		Instant createdAt
) {
}
