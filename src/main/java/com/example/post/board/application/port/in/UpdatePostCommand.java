package com.example.post.board.application.port.in;

public record UpdatePostCommand(
		Long postId,
		String title,
		String content,
		Long requesterMemberId
) {
}
