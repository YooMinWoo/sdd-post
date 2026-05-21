package com.example.post.board.application.port.in;

public record ListPostCommentsQuery(Long postId, Integer page, Integer size) {
}
