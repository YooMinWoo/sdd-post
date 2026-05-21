package com.example.post.board.application.port.in;

public record UpdateCommentCommand(
		Long postId,
		Long commentId,
		String content,
		Long requesterMemberId
) {
}
