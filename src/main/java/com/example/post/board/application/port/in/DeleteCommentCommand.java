package com.example.post.board.application.port.in;

public record DeleteCommentCommand(
		Long postId,
		Long commentId,
		Long requesterMemberId
) {
}
