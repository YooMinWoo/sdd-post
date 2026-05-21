package com.example.post.board.application.port.in;

public record CreateCommentCommand(Long postId, String content, Long authorMemberId) {
}
