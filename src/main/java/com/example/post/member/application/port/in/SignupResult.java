package com.example.post.member.application.port.in;

import java.time.Instant;

public record SignupResult(Long id, String email, String nickname, Instant createdAt) {
}
