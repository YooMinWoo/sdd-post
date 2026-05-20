package com.example.post.member.application.port.out;

public record AccessTokenMemberClaims(Long memberId, String email, String nickname) {
}
