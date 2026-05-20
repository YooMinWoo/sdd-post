package com.example.post.board.adapter.in.web;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "게시글 목록 항목 응답")
public record PostSummaryResponse(
		@Schema(description = "게시글 식별자", example = "1")
		Long id,

		@Schema(description = "게시글 제목", example = "안녕하세요")
		String title,

		@Schema(description = "작성자 회원 식별자", example = "1")
		Long authorMemberId,

		@Schema(description = "작성자 현재 닉네임", example = "minu")
		String author,

		@Schema(description = "게시글 생성 시각", example = "2026-05-20T00:00:00Z")
		Instant createdAt
) {
}
