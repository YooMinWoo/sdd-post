package com.example.post.member.application.port.in;

public interface RefreshTokenUseCase {

	TokenResult refresh(RefreshTokenCommand command);
}
