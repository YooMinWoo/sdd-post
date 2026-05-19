package com.example.post.member.application.port.in;

public record SignupCommand(String email, String password, String nickname) {
}
