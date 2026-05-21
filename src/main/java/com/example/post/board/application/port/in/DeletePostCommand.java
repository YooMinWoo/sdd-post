package com.example.post.board.application.port.in;

public record DeletePostCommand(Long postId, Long requesterMemberId) {
}
