package com.example.post.member.application.port.out;

public interface PasswordMatcherPort {

	boolean matches(String rawPassword, String encodedPassword);
}
