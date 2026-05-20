package com.example.post.member.application.service;

import com.example.post.member.application.exception.InvalidRefreshTokenException;
import com.example.post.member.application.port.in.LogoutCommand;
import com.example.post.member.application.port.in.LogoutUseCase;
import com.example.post.member.application.port.out.RefreshTokenStorePort;
import com.example.post.member.application.port.out.TokenProviderPort;
import org.springframework.stereotype.Service;

@Service
public class LogoutService implements LogoutUseCase {

	private final TokenProviderPort tokenProviderPort;
	private final RefreshTokenStorePort refreshTokenStorePort;

	public LogoutService(TokenProviderPort tokenProviderPort, RefreshTokenStorePort refreshTokenStorePort) {
		this.tokenProviderPort = tokenProviderPort;
		this.refreshTokenStorePort = refreshTokenStorePort;
	}

	@Override
	public void logout(LogoutCommand command) {
		try {
			Long memberId = tokenProviderPort.extractRefreshTokenMemberId(command.refreshToken());
			refreshTokenStorePort.deleteByMemberId(memberId);
		}
		catch (InvalidRefreshTokenException ignored) {
			// 로그아웃은 멱등 API이므로 만료/무효 refreshToken도 성공으로 취급한다.
		}
	}
}
