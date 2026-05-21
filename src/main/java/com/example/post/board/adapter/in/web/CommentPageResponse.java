package com.example.post.board.adapter.in.web;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "댓글 페이지 응답")
public record CommentPageResponse(
		@Schema(description = "댓글 목록")
		List<CommentSummaryResponse> items,

		@Schema(description = "현재 댓글 페이지 번호", example = "0")
		int page,

		@Schema(description = "댓글 페이지 크기", example = "10")
		int size,

		@Schema(description = "전체 댓글 수", example = "2")
		long totalElements,

		@Schema(description = "전체 댓글 페이지 수", example = "1")
		int totalPages,

		@Schema(description = "첫 댓글 페이지 여부", example = "true")
		boolean first,

		@Schema(description = "마지막 댓글 페이지 여부", example = "true")
		boolean last
) {
}
