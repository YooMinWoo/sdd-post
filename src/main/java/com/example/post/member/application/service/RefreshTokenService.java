package com.example.post.member.application.service;

import com.example.post.member.application.exception.InvalidRefreshTokenException;
import com.example.post.member.application.port.in.RefreshTokenCommand;
import com.example.post.member.application.port.in.RefreshTokenUseCase;
import com.example.post.member.application.port.in.TokenResult;
import com.example.post.member.application.port.out.MemberRepositoryPort;
import com.example.post.member.application.port.out.RefreshTokenStorePort;
import com.example.post.member.application.port.out.TokenProviderPort;
import com.example.post.member.domain.model.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefreshTokenService implements RefreshTokenUseCase {

	private final MemberRepositoryPort memberRepositoryPort;
	private final TokenProviderPort tokenProviderPort;
	private final RefreshTokenStorePort refreshTokenStorePort;

	public RefreshTokenService(
			MemberRepositoryPort memberRepositoryPort,
			TokenProviderPort tokenProviderPort,
			RefreshTokenStorePort refreshTokenStorePort
	) {
		this.memberRepositoryPort = memberRepositoryPort;
		this.tokenProviderPort = tokenProviderPort;
		this.refreshTokenStorePort = refreshTokenStorePort;
	}

	@Override
	@Transactional(readOnly = true)
	public TokenResult refresh(RefreshTokenCommand command) {
		Long memberId = tokenProviderPort.extractRefreshTokenMemberId(command.refreshToken());
		String storedRefreshToken = refreshTokenStorePort.findByMemberId(memberId)
				.orElseThrow(InvalidRefreshTokenException::new);
		if (!storedRefreshToken.equals(command.refreshToken())) {
			throw new InvalidRefreshTokenException();
		}

		Member member = memberRepositoryPort.findById(memberId)
				.orElseThrow(InvalidRefreshTokenException::new);
		TokenResult tokenResult = tokenProviderPort.issue(member);
		refreshTokenStorePort.save(memberId, tokenResult.refreshToken(), tokenProviderPort.refreshTokenTtl());
		return tokenResult;
	}
}
