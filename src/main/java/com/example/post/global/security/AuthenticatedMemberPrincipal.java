package com.example.post.global.security;

public record AuthenticatedMemberPrincipal(Long id, String email, String nickname) {
}
