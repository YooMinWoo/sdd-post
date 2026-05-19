package com.example.post.application.port.in;

public record SignupCommand(String email, String password, String nickname) {
}
