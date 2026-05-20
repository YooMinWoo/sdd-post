package com.example.post.board.adapter.in.web;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "게시글 목록 조회 응답")
public record ListPostsResponse(
		@Schema(description = "게시글 목록. 본문은 포함하지 않습니다.")
		List<PostSummaryResponse> posts,

		@Schema(description = "현재 페이지 번호. 0부터 시작합니다.", example = "0")
		int page,

		@Schema(description = "페이지 크기", example = "10")
		int size,

		@Schema(description = "전체 게시글 수", example = "42")
		long totalElements,

		@Schema(description = "전체 페이지 수", example = "5")
		int totalPages,

		@Schema(description = "첫 페이지 여부", example = "true")
		boolean first,

		@Schema(description = "마지막 페이지 여부", example = "false")
		boolean last
) {
	public ListPostsResponse {
		posts = List.copyOf(posts);
	}
}
