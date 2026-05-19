package com.example.post.application.port.in;

import java.time.Instant;

public record CreatePostResult(Long id, String title, String content, String author, Instant createdAt) {
}
