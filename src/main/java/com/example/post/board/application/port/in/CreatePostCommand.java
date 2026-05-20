package com.example.post.board.application.port.in;

public record CreatePostCommand(String title, String content, Long authorMemberId) {
}
