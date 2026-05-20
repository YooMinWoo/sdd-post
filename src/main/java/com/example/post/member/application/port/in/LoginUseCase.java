package com.example.post.member.application.port.in;

public interface LoginUseCase {

	TokenResult login(LoginCommand command);
}
